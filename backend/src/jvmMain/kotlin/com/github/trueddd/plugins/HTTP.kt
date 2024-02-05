package com.github.trueddd.plugins

import com.github.trueddd.utils.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.partialcontent.*

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHost(
            host = Environment.ClientAddress,
            schemes = if (Environment.IsDev) listOf("ws", "http") else listOf("wss", "https")
        )
    }
    install(ConditionalHeaders)
    install(PartialContent)
    install(AutoHeadResponse)
}
