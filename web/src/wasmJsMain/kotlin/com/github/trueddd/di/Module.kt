package com.github.trueddd.di

import com.github.trueddd.core.*
import com.github.trueddd.utils.serialization
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.binds
import org.koin.dsl.module

val module = module {

    single {
        HttpClient(Js) {
            install(WebSockets)
            install(ContentNegotiation) {
                json(serialization)
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

    single { GameStateProviderImpl(httpClient = get(), router = get(), authManager = get(), appClient = get()) }
        .binds(arrayOf(GameStateProvider::class, CommandSender::class))
}
