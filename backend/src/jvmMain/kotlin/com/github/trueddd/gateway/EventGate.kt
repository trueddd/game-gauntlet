package com.github.trueddd.gateway

import com.github.trueddd.core.Command
import com.github.trueddd.core.EventGate
import com.github.trueddd.core.Response
import com.github.trueddd.core.Router
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.validateWebSocketsAuth
import com.github.trueddd.utils.webSocketCloseReason
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.awaitCancellation
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

    webSocket(Router.STATE) {
        val user = validateWebSocketsAuth(eventGate.stateHolder) {
            close(it.webSocketCloseReason())
        } ?: return@webSocket
        eventGate.stateHolder.globalStateFlow
            .onStart { Log.info(TAG, "Starting $user`s session ${this@webSocket}") }
            .map { it.stateSnapshot }
            .distinctUntilChanged()
            .map { Response.State(it) }
            .onEach { outgoing.sendResponse(it) }
            .onCompletion { Log.info(TAG, "Ending $user`s session ${this@webSocket}") }
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
                        eventGate.eventManager.startHandling(restored.globalState, restored.playersHistory)
                    }
                    is Command.Action -> eventGate.parseAndHandle(command.payload)
                    is Command.Reset -> eventGate.resetState()
                    null -> outgoing.sendErrorResponse("Unrecognised command: `$text`")
                }
            }
        }
    }

    webSocket(Router.ACTIONS) {
        eventGate.historyHolder.actionsChannel
            .onEach { outgoing.sendResponse(Response.UserAction(it)) }
            .launchIn(this)
        awaitCancellation()
    }
}

suspend fun SendChannel<Frame>.sendResponse(response: Response) {
    send(Frame.Text(response.serialized))
}

private suspend fun SendChannel<Frame>.sendErrorResponse(error: String) {
    send(Frame.Text(Response.Error(Exception(error)).serialized))
}
