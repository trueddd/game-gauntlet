package com.github.trueddd.di

import com.github.trueddd.core.AppClient
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val module = module {

    single {
        Json {
            allowStructuredMapKeys = true
            prettyPrint = true
        }
    }

    single {
        HttpClient(Js) {
            install(WebSockets)
            install(ContentNegotiation) {
                json(get<Json>())
            }
        }
    }

    single { AppClient(httpClient = get(), decoder = get()) }
}
