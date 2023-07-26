package com.github.trueddd.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        staticFiles("/icons", File("src/main/resources/icons"))
    }
}
