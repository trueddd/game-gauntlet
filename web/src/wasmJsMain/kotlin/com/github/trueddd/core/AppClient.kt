package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.*
import com.github.trueddd.data.request.DownloadGameRequestBody
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL

class AppClient(
    private val httpClient: HttpClient,
    private val router: ServerRouter,
) : CoroutineScope {

    override val coroutineContext by lazy {
        Dispatchers.Default + SupervisorJob()
    }

    private fun savedJwtToken(): String? {
        return window.localStorage.getItem(AuthManager.TOKEN_KEY)
    }

    fun getActionsFlow(): Flow<List<Action>> {
        return callbackFlow {
            send(loadActions())
            val session = httpClient.webSocketSession(router.ws(Router.ACTIONS))
            launch {
                for (frame in session.incoming) {
                    val textFrame = frame as? Frame.Text ?: continue
                    val content = textFrame.readText()
                    println("New action received: $content")
                    val data = Response.parse(textFrame.readText()) ?: continue
                    if (data is Response.UserAction) {
                        this@callbackFlow.send(listOf(data.action))
                    }
                }
            }
            awaitClose {
                session.cancel()
            }
        }
    }

    private suspend fun loadActions(): List<Action> =
        getJsonData(router.http(Router.ACTIONS), sendBearerToken = true) ?: emptyList()

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

    suspend fun getGameConfig(): GameConfig? = getJsonData(router.http(Router.CONFIG))

    suspend fun getStateSnapshot(): StateSnapshot? = getJsonData(router.http(Router.SNAPSHOT))

    suspend fun getPlayersHistory(): PlayersHistory? =
        getJsonData(router.http(Router.TURNS), sendBearerToken = true)

    suspend fun getGames(): List<Game> =
        getJsonData(router.http(Router.Wheels.GAMES), sendBearerToken = true) ?: emptyList()

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
                        savedJwtToken()?.let { bearerAuth(it) }
                    }
                    contentType(ContentType.Application.Json)
                }.body<T>()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
