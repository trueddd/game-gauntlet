package com.github.trueddd.gateway

import com.github.trueddd.core.EventGate
import com.github.trueddd.utils.Log
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.ktor.ext.inject

private const val TAG = "EventGate"

fun Routing.setupEventGate() {
    val eventGate by inject<EventGate>()
    webSocket("/state") {
        eventGate.stateHolder.globalStateFlow
            .onStart { Log.info(TAG, "Listening for global state in session ${this@webSocket}") }
            .onEach { outgoing.send(Frame.Text("New game state: $it")) }
            .launchIn(this)
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                outgoing.send(Frame.Text("YOU SAID: $text"))
                eventGate.inputParser.parse(text)?.let {
                    eventGate.eventManager.consumeAction(it)
                    if (!it.singleShot) {
                        eventGate.historyHolder.pushEvent(it)
                    }
                }
                when (text) {
                    "start" -> eventGate.eventManager.startHandling()
                    "bye" -> close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    "save" -> eventGate.historyHolder.save()
                    "restore" -> {
                        eventGate.eventManager.stopHandling()
                        val restored = eventGate.historyHolder.load()
                        eventGate.eventManager.startHandling(initState = restored)
                    }
                }
            }
        }
    }
}
