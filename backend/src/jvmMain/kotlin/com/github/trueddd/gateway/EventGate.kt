package com.github.trueddd.gateway

import com.github.trueddd.core.Command
import com.github.trueddd.core.EventGate
import com.github.trueddd.core.Response
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.Log
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

private const val TAG = "EventGate"

fun Routing.setupEventGate() {
    val eventGate by inject<EventGate>()
    application.launch {
        eventGate.start()
    }

    webSocket("/state") {
        eventGate.stateHolder.globalStateFlow
            .onStart { Log.info(TAG, "Listening to global state in session ${this@webSocket}") }
            .map { Response.State(it) }
            .onEach { outgoing.sendResponse(it) }
            .onCompletion { Log.info(TAG, "Ending global state session ${this@webSocket}") }
            .launchIn(this)
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                if (Environment.IsDev) {
                    outgoing.sendResponse(Response.Info("YOU SAID: $text"))
                }
                when (val command = Command.parseCommand(text)) {
                    is Command.Start -> eventGate.eventManager.startHandling()
                    is Command.Disconnect -> close(CloseReason(
                        CloseReason.Codes.NORMAL,
                        "Client said BYE"
                    ))
                    is Command.Save -> eventGate.historyHolder.save(eventGate.stateHolder.current)
                    is Command.Restore -> {
                        eventGate.eventManager.stopHandling()
                        val restored = eventGate.historyHolder.load()
                        eventGate.eventManager.startHandling(initState = restored)
                    }
                    is Command.Action -> eventGate.parseAndHandle(command.payload)
                    is Command.Reset -> eventGate.resetState()
                    null -> outgoing.sendResponse(Response.Error(Exception("Unrecognised command: `$text`")))
                }
            }
        }
    }

    webSocket("/actions") {
        for (action in eventGate.historyHolder.actionsChannel) {
            outgoing.sendResponse(Response.UserAction(action))
        }
    }
}

private suspend fun SendChannel<Frame>.sendResponse(response: Response) {
    send(Frame.Text(response.serialized))
}
