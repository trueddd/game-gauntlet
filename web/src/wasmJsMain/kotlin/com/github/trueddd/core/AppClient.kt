package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.AuthResponse
import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.request.DownloadGameRequestBody
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.util.toBlob
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL

class AppClient(
    private val httpClient: HttpClient,
) : CoroutineScope {

    val router = ServerRouter()

    private var runnerJob: Job? = null

    private val _globalState = MutableStateFlow<GlobalState?>(null)
    val globalState: StateFlow<GlobalState?>
        get() = _globalState.asStateFlow()

    private val _connectionState = MutableStateFlow<SocketState>(SocketState.Disconnected())
    val connectionState: StateFlow<SocketState>
        get() = _connectionState.asStateFlow()

    private val actionsChannel = Channel<String>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val coroutineContext by lazy {
        Dispatchers.Default + SupervisorJob()
    }

    fun sendCommand(command: Command) {
        launch {
            println("sending `${command.value}`")
            actionsChannel.send(command.value)
        }
    }

    private fun savedJwtToken(): String? {
        return window.localStorage.getItem(AuthManager.TOKEN_KEY)
    }

    fun start() {
        if (runnerJob?.isActive == true) {
            println("Client is already running")
            return
        }
        runnerJob = launch {
            _connectionState.value = SocketState.Connecting
            httpClient.webSocket(router.ws(Router.STATE)) {
                val token = savedJwtToken() ?: run {
                    close()
                    return@webSocket
                }
                outgoing.send(Frame.Text(token))
                _connectionState.value = SocketState.Connected
                launch {
                    for (action in actionsChannel) {
                        outgoing.send(Frame.Text(action))
                    }
                }
                for (frame in incoming) {
                    val textFrame = frame as? Frame.Text ?: continue
                    val data = Response.parse(textFrame.readText().also { println(it) }) ?: continue
                    when (data) {
                        is Response.UserAction -> continue
                        is Response.Error -> println("Error occurred: ${data.exception.message}")
                        is Response.Info -> println("Message from server: ${data.message}")
                        is Response.State -> _globalState.emit(data.globalState)
                    }
                }
                if (runnerJob?.isActive == true) {
                    val reason = closeReason.await()?.message ?: "Unknown reason"
                    if (reason == Response.ErrorCode.AuthError || reason == Response.ErrorCode.TokenExpired) {
                        get<AuthManager>().logout()
                    }
                    runnerJob?.cancel(reason)
                }
            }
        }
        runnerJob?.invokeOnCompletion { throwable ->
            val error = throwable?.let { Error(it) }
            _connectionState.value = SocketState.Disconnected(error)
        }
    }

    fun stop() {
        runnerJob?.cancel()
        runnerJob = null
    }

    fun getActionsFlow(): Flow<List<Action>> {
        return callbackFlow {
            send(loadActions())
            val session = httpClient.webSocketSession(router.ws(Router.ACTIONS))
            launch {
                for (frame in session.incoming) {
                    val textFrame = frame as? Frame.Text ?: continue
                    val data = Response.parse(textFrame.readText()) ?: continue
                    if (data is Response.UserAction) {
                        println("New action received: ${data.action}")
                        this@callbackFlow.send(listOf(data.action))
                    }
                }
            }
            awaitClose {
                session.cancel()
            }
        }
    }

    private suspend fun loadActions(): List<Action> {
        return withContext(coroutineContext) {
            try {
                httpClient.get(router.http(Router.ACTIONS)) {
                    bearerAuth(savedJwtToken()!!)
                    contentType(ContentType.Application.Json)
                }.body()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun loadImage(url: String): ByteArray? {
        return withContext(coroutineContext) {
            try {
                httpClient.get(url) {
                    contentType(ContentType.Image.PNG)
                }.bodyAsChannel().toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun searchGame(name: String) {
        launch {
            try {
                val response = httpClient.post(router.http(Router.LOAD_GAME)) {
                    contentType(ContentType.Application.Json)
                    setBody(DownloadGameRequestBody(name))
                    onDownload { bytesSentTotal, contentLength ->
                        println("Received $bytesSentTotal bytes from $contentLength")
                    }
                }
                val body = response.body<ByteArray>()
                println("Received ${body.size} bytes in total")
                val link = document.createElement("a") as HTMLAnchorElement
                link.setAttribute("href", URL.createObjectURL(body.toBlob()))
                link.setAttribute("download", "$name.exe")
                link.click()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun verifyUser(token: String): Result<AuthResponse> {
        return withContext(coroutineContext) {
            try {
                httpClient.post(router.http(Router.USER)) {
                    contentType(ContentType.Application.Json)
                    parameter("token", token)
                }.body<AuthResponse>().let { Result.success(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun getItems(): List<WheelItem> =
        getJsonData(router.http(Router.Wheels.ITEMS)) ?: emptyList()

    suspend fun getGames(): List<Game> =
        getJsonData(router.http(Router.Wheels.GAMES)) ?: emptyList()

    suspend fun getPlayers(): List<Participant> =
        getJsonData(router.http(Router.Wheels.PLAYERS), sendBearerToken = true) ?: emptyList()

    suspend fun rollItem(): WheelItem? =
        getJsonData(router.http(Router.Wheels.ROLL_ITEMS), sendBearerToken = true)

    suspend fun rollGame(): Game? =
        getJsonData(router.http(Router.Wheels.ROLL_GAMES), sendBearerToken = true)

    suspend fun rollPlayer(): Participant? =
        getJsonData(router.http(Router.Wheels.ROLL_PLAYERS), sendBearerToken = true)

    private suspend inline fun <reified T> getJsonData(
        urlString: String,
        sendBearerToken: Boolean = false
    ): T? {
        return withContext(coroutineContext) {
            try {
                httpClient.get(urlString) {
                    if (sendBearerToken) {
                        bearerAuth(savedJwtToken()!!)
                    }
                    contentType(ContentType.Application.Json)
                }.body<T>()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // TODO: accept rolled wheel item in dialog
    fun acceptRolledItem(item: WheelItem, user: Participant) {
        sendCommand(Command.Action("${user.name}:${Action.Key.ItemReceive}:${item.id.value}"))
    }
}
