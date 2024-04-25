package com.github.trueddd.plugins

import com.github.trueddd.utils.serialization
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(serialization)
    }
}
