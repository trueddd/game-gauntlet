package com.github.trueddd.core

import com.github.trueddd.data.model.*
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

    private companion object {
        const val CLIENT_ID_HEADER = "Client-Id"
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(serialization)
        }
    }

    suspend fun validateTwitchToken(token: String): Result<TwitchTokenValidationSuccess> {
        return withContext(Dispatchers.IO) {
            try {
                client.get(Url("https://id.twitch.tv/oauth2/validate")) {
                    header(CLIENT_ID_HEADER, Environment.TwitchClientId)
                    bearerAuth(token)
                }.body<TwitchTokenValidationSuccess>().let { Result.success(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun getTwitchUser(token: String): Result<TwitchUser> {
        return withContext(Dispatchers.IO) {
            try {
                client.get(Url("https://api.twitch.tv/helix/users")) {
                    header(CLIENT_ID_HEADER, Environment.TwitchClientId)
                    bearerAuth(token)
                }.body<TwitchResponse<List<TwitchUser>>>().data.first().let { Result.success(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun createTwitchReward(
        broadcasterId: String,
        token: String,
    ): Result<TwitchReward> {
        return withContext(Dispatchers.IO) {
            try {
                client.post(Url("https://api.twitch.tv/helix/channel_points/custom_rewards")) {
                    header("client-id", Environment.TwitchClientId)
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    parameter("broadcaster_id", broadcasterId)
                    setBody(TwitchRewardCreationRequest(
                        title = "Глобальные события AGG2",
                        cost = 5000,
                    ))
                }.body<TwitchResponse<List<TwitchReward>>>().data.let { Result.success(it.first()) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun fetchRedemptions(
        broadcasterId: String,
        rewardId: String,
        token: String,
        after: String?,
        pageSize: Int,
    ): Result<PaginatedTwitchResponse<List<RewardRedemption>>> {
        return withContext(Dispatchers.IO) {
            try {
                client.get(Url("https://api.twitch.tv/helix/channel_points/custom_rewards/redemptions")) {
                    header(CLIENT_ID_HEADER, Environment.TwitchClientId)
                    bearerAuth(token)
                    parameter("broadcaster_id", broadcasterId)
                    parameter("reward_id", rewardId)
                    parameter("status", "UNFULFILLED")
                    parameter("first", pageSize)
                    after?.let { parameter("after", it) }
                }.body<PaginatedTwitchResponse<List<RewardRedemption>>>().let { Result.success(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
