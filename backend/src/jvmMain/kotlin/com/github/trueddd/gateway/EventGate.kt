package com.github.trueddd.gateway

import com.auth0.jwt.exceptions.TokenExpiredException
import com.github.trueddd.core.Command
import com.github.trueddd.core.EventGate
import com.github.trueddd.core.Response
import com.github.trueddd.core.Router
import com.github.trueddd.plugins.Jwt
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.Log
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
        val token = (incoming.receive() as? Frame.Text)?.readText()
        val user = try {
            val decoded = Jwt.Verifier.verify(token)
            val userName = decoded.getClaim("user").asString()
            eventGate.stateHolder.participants.first { it.name == userName }
        } catch (e: TokenExpiredException) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, Response.ErrorCode.TokenExpired))
            return@webSocket
        } catch (e: Exception) {
            e.printStackTrace()
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, Response.ErrorCode.AuthError))
            return@webSocket
        }
        eventGate.stateHolder.globalStateFlow
            .onStart { Log.info(TAG, "Starting $user`s session ${this@webSocket}") }
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
                        eventGate.eventManager.startHandling(initState = restored)
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

private suspend fun SendChannel<Frame>.sendResponse(response: Response) {
    send(Frame.Text(response.serialized))
}

private suspend fun SendChannel<Frame>.sendErrorResponse(error: String) {
    send(Frame.Text(Response.Error(Exception(error)).serialized))
}
