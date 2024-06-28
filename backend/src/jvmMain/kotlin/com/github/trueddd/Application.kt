package com.github.trueddd

import com.github.trueddd.plugins.configureDI
import com.github.trueddd.plugins.configureHTTP
import com.github.trueddd.plugins.configureMonitoring
import com.github.trueddd.plugins.configureSecurity
import com.github.trueddd.plugins.configureSerialization
import com.github.trueddd.plugins.configureSockets
import com.github.trueddd.plugins.configureRouting
import com.github.trueddd.plugins.twitchIntegration
import com.github.trueddd.utils.Environment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

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
