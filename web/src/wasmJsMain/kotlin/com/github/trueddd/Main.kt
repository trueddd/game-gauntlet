package com.github.trueddd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.request.DownloadGameRequestBody
import com.github.trueddd.utils.WebEnvironment
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL

private suspend fun HttpClient.searchGame(name: String) {
    try {
        val response = post("http://localhost:${WebEnvironment.ServerPort}/game") {
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

@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
fun main() {
    val decoder = Json {
        allowStructuredMapKeys = true
        prettyPrint = true
    }
    val httpClient = HttpClient(Js) {
        install(WebSockets)
        install(ContentNegotiation) {
            json(decoder)
        }
    }
    val globalState = MutableStateFlow<GlobalState?>(null)
    val actionsChannel = Channel<String>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    CanvasBasedWindow("AGG2", canvasElementId = "canvas") {
        LaunchedEffect(Unit) {
            httpClient.webSocket("ws://localhost:${WebEnvironment.ServerPort}/state") {
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
                    decoder.decodeFromString<GlobalState>(data).let { globalState.value = it }
                }
            }
        }
        val state by globalState.collectAsState()
        if (state != null) {
            MaterialTheme {
                CompositionLocalProvider(LocalContentColor provides Colors.Text) {
                    App(
                        globalState = state!!,
                        onActionSent = { actionsChannel.trySend(it) },
                        onSearchRequested = { GlobalScope.launch { httpClient.searchGame(it) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun App(
    globalState: GlobalState,
    onActionSent: (String) -> Unit = {},
    onSearchRequested: (String) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Background)
            .padding(16.dp)
    ) {
        ActionsBoardW(globalState, onActionSent)
        StateTableW(globalState)
        ArchivesW(onSearchRequested)
    }
}
