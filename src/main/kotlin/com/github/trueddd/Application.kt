package com.github.trueddd

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.github.trueddd.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureDI()
        configureSockets()
        configureSerialization()
        configureMonitoring()
        configureHTTP()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
