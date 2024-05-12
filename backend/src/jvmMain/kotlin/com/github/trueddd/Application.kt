package com.github.trueddd

import com.github.trueddd.plugins.*
import com.github.trueddd.utils.Environment
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = Environment.Port, host = "0.0.0.0") {
        configureDI()
        configureMonitoring()
        configureSecurity()
        configureSerialization()
        configureHTTP()
        configureSockets()
        configureRouting()
        twitchIntegration()
    }.start(wait = true)
}
