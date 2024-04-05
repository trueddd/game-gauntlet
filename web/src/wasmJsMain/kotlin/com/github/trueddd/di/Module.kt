package com.github.trueddd.di

import com.github.trueddd.core.AppClient
import com.github.trueddd.core.AppStorage
import com.github.trueddd.core.AuthManager
import com.github.trueddd.core.ServerRouter
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
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
            createClientPlugin("Auth") {
                on(Send) { request ->
                    val originalCall = proceed(request)
                    if (originalCall.response.status == HttpStatusCode.Unauthorized) {
                        get<AuthManager>().logout()
                    }
                    originalCall
                }
            }
        }
    }

    single { ServerRouter() }

    single { AppClient(httpClient = get(), router = get()) }

    single { AuthManager(appClient = get()) }

    single { AppStorage() }
}
