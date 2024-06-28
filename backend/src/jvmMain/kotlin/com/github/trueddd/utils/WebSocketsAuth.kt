package com.github.trueddd.utils

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.github.trueddd.core.Response
import com.github.trueddd.core.StateHolder
import com.github.trueddd.data.Participant
import com.github.trueddd.plugins.security.Jwt
import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

fun Exception.webSocketCloseReason(): CloseReason {
    return when (this) {
        is TokenExpiredException -> CloseReason(CloseReason.Codes.CANNOT_ACCEPT, Response.ErrorCode.TokenExpired)
        else -> CloseReason(CloseReason.Codes.CANNOT_ACCEPT, Response.ErrorCode.AuthError)
    }
}

suspend inline fun DefaultWebSocketSession.validateWebSocketsAuth(
    stateHolder: StateHolder,
    crossinline onError: suspend (Exception) -> Unit,
): Participant? {
    val token = (incoming.receive() as? Frame.Text)?.readText()
    return try {
        val decoded = Jwt.Verifier.verify(token)
        val userName = decoded.getClaim("user").asString()
        stateHolder.participants.first { it.name == userName }
    } catch (e: JWTVerificationException) {
        Log.error("WebSocketsAuth", "Failed to validate token")
        onError(e)
        null
    }
}
