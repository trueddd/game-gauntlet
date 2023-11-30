package com.github.trueddd.gateway

import com.github.trueddd.core.EventGate
import com.github.trueddd.utils.Log
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

private const val TAG = "EventGate"

fun Routing.setupEventGate() {
    val eventGate by inject<EventGate>()
    webSocket("/state") {
        val encoder = Json {
            allowStructuredMapKeys = true
        }
        val verbalState = call.parameters.contains("verbal", "1")
        eventGate.stateHolder.globalStateFlow
            .onStart { Log.info(TAG, "Listening for global state in session ${this@webSocket}") }
            .onEach { state ->
                val encoded = if (verbalState) {
                    "New game state: $state"
                } else {
                    encoder.encodeToString(state)
                }
                outgoing.send(Frame.Text(encoded))
            }
            .launchIn(this)
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                outgoing.send(Frame.Text("YOU SAID: $text"))
                val handled = eventGate.parseAndHandle(text)
                if (handled) {
                    continue
                }
                when (text) {
                    "start" -> eventGate.eventManager.startHandling()
                    "bye" -> close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    "save" -> eventGate.historyHolder.save(eventGate.stateHolder.current)
                    "restore" -> {
                        eventGate.eventManager.stopHandling()
                        val restored = eventGate.historyHolder.load()
                        eventGate.eventManager.startHandling(initState = restored)
                    }
                    else -> outgoing.send(Frame.Text("Unrecognised command: $text"))
                }
            }
        }
    }
}
