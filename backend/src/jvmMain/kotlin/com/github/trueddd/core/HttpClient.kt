package com.github.trueddd.core

import com.github.trueddd.data.TwitchResponse
import com.github.trueddd.data.TwitchUser
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.serialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class HttpClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(serialization)
        }
    }

    suspend fun getTwitchUser(token: String): Result<TwitchUser> {
        return withContext(Dispatchers.IO) {
            try {
                client.get(Url("https://api.twitch.tv/helix/users")) {
                header("Client-Id", Environment.TwitchClientId)
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<TwitchResponse<List<TwitchUser>>>().data.first().let { Result.success(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
