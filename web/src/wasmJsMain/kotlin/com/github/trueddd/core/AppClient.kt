package com.github.trueddd.core

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.request.DownloadGameRequestBody
import com.github.trueddd.items.WheelItem
import com.github.trueddd.util.*
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
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL

class AppClient(
    private val httpClient: HttpClient,
    private val decoder: Json,
) : CoroutineScope {

    private var runnerJob: Job? = null

    private val _globalState = MutableStateFlow<GlobalState?>(null)
    val globalState: StateFlow<GlobalState?>
        get() = _globalState.asStateFlow()

    private val _connectionState = MutableStateFlow<SocketState>(SocketState.Disconnected())
    val connectionState: StateFlow<SocketState>
        get() = _connectionState.asStateFlow()

    private val actionsChannel = Channel<String>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun getWheelItemIconUrl(id: Int): String {
        return "$httpProtocol://${serverAddress()}/icons/$id.png"
    }

    override val coroutineContext by lazy {
        Dispatchers.Default + SupervisorJob()
    }

    fun sendAction(action: String) {
        launch {
            actionsChannel.send(action)
        }
    }

    fun start() {
        if (runnerJob?.isActive == true) {
            println("Client is already running")
            return
        }
        runnerJob = launch {
            _connectionState.value = SocketState.Connecting
            httpClient.webSocket("$wsProtocol://${serverAddress()}/state") {
                _connectionState.value = SocketState.Connected
                launch {
                    for (action in actionsChannel) {
                        outgoing.send(Frame.Text(action))
                    }
                }
                for (frame in incoming) {
                    val textFrame = frame as? Frame.Text ?: continue
                    val data = textFrame.readText()
                    if (data.startsWith("YOU SAID")) {
                        continue
                    }
                    println("Received data: $data")
                    decoder.decodeFromString<GlobalState>(data).let { _globalState.value = it }
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
                val response = httpClient.post("$httpProtocol://${serverAddress()}/game") {
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

    suspend fun getItems(): List<WheelItem> {
        return withContext(coroutineContext) {
            try {
                httpClient.get("$httpProtocol://${serverAddress()}/items") {
                    contentType(ContentType.Application.Json)
                }.body<List<WheelItem>>()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
