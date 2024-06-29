package com.github.trueddd.plugins

import com.github.trueddd.gateway.setupEventGate
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import java.time.Duration

private const val PING_PERIOD_SECONDS = 15L
private const val TIMEOUT_SECONDS = 15L

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(PING_PERIOD_SECONDS)
        timeout = Duration.ofSeconds(TIMEOUT_SECONDS)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        setupEventGate()
    }
}
