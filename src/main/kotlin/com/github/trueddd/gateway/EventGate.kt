package com.github.trueddd.gateway

import com.github.trueddd.core.EventManager
import com.github.trueddd.core.InputParser
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.ktor.ext.inject

fun Routing.setupEventGate() {
    val eventManager by inject<EventManager>()
    val inputParser by inject<InputParser>()
    webSocket("/state") {
        eventManager.globalStateFlow
            .onStart { println("Listening for global state in session ${this@webSocket}") }
            .onEach { outgoing.send(Frame.Text("New game state: $it")) }
            .launchIn(this)
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                outgoing.send(Frame.Text("YOU SAID: $text"))
                inputParser.parse(text)?.let { eventManager.consumeAction(it) }
                when (text) {
                    "bye" -> close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    "save" -> eventManager.save()
                    "restore" -> eventManager.restore()
                }
            }
        }
    }
}
