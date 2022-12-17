package com.github.trueddd.gateway

import com.github.trueddd.core.EventManager
import com.github.trueddd.core.InputParser
import com.github.trueddd.core.StateHolder
import com.github.trueddd.core.history.EventHistoryHolder
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
    val stateHolder by inject<StateHolder>()
    val eventManager by inject<EventManager>()
    val inputParser by inject<InputParser>()
    val eventHistoryHolder by inject<EventHistoryHolder>()
    webSocket("/state") {
        stateHolder.globalStateFlow
            .onStart { Log.info(TAG, "Listening for global state in session ${this@webSocket}") }
            .onEach { outgoing.send(Frame.Text("New game state: $it")) }
            .launchIn(this)
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                outgoing.send(Frame.Text("YOU SAID: $text"))
                inputParser.parse(text)?.let {
                    eventManager.consumeAction(it)
                    if (!it.singleShot) {
                        eventHistoryHolder.pushEvent(it)
                    }
                }
                when (text) {
                    "start" -> eventManager.startHandling()
                    "bye" -> close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    "save" -> eventHistoryHolder.save()
                    "restore" -> {
                        eventManager.stopHandling()
                        val restored = eventHistoryHolder.load()
                        eventManager.startHandling(initState = restored)
                    }
                }
            }
        }
    }
}
