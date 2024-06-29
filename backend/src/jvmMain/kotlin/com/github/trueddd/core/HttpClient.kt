package com.github.trueddd.core

import com.github.trueddd.data.model.PaginatedTwitchResponse
import com.github.trueddd.data.model.RewardRedemption
import com.github.trueddd.data.model.TwitchResponse
import com.github.trueddd.data.model.TwitchReward
import com.github.trueddd.data.model.TwitchRewardCreationRequest
import com.github.trueddd.data.model.TwitchTokenValidationSuccess
import com.github.trueddd.data.model.TwitchUser
import com.github.trueddd.di.CoroutineDispatchers
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.GlobalEventConstants
import com.github.trueddd.utils.serialization
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toByteArray
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
class HttpClient(
    dispatchers: CoroutineDispatchers,
) {

    private companion object {
        const val CLIENT_ID_HEADER = "Client-Id"
    }

    private val coroutineContext: CoroutineContext = SupervisorJob() + dispatchers.io

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(serialization)
        }
    }

    suspend fun validateTwitchToken(token: String): Result<TwitchTokenValidationSuccess> {
        return safeRequest {
            client.get(Url("https://id.twitch.tv/oauth2/validate")) {
                header(CLIENT_ID_HEADER, Environment.TwitchClientId)
                bearerAuth(token)
            }.body()
        }
    }

    suspend fun getTwitchUser(token: String): Result<TwitchUser> {
        return safeRequest {
            client.get(Url("https://api.twitch.tv/helix/users")) {
                header(CLIENT_ID_HEADER, Environment.TwitchClientId)
                bearerAuth(token)
            }.body<TwitchResponse<List<TwitchUser>>>().data.first()
        }
    }

    suspend fun createTwitchReward(
        broadcasterId: String,
        token: String,
    ): Result<TwitchReward> {
        return safeRequest {
            client.post(Url("https://api.twitch.tv/helix/channel_points/custom_rewards")) {
                header("client-id", Environment.TwitchClientId)
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                parameter("broadcaster_id", broadcasterId)
                setBody(
                    TwitchRewardCreationRequest(
                        title = "Глобальные события AGG2",
                        cost = GlobalEventConstants.SINGLE_REDEMPTION.toInt(),
                    )
                )
            }.body()
        }
    }

    suspend fun fetchRedemptions(
        broadcasterId: String,
        rewardId: String,
        token: String,
        after: String?,
        pageSize: Int,
    ): Result<PaginatedTwitchResponse<List<RewardRedemption>>> {
        return safeRequest {
            client.get(Url("https://api.twitch.tv/helix/channel_points/custom_rewards/redemptions")) {
                header(CLIENT_ID_HEADER, Environment.TwitchClientId)
                bearerAuth(token)
                parameter("broadcaster_id", broadcasterId)
                parameter("reward_id", rewardId)
                parameter("status", "UNFULFILLED")
                parameter("first", pageSize)
                after?.let { parameter("after", it) }
            }.body()
        }
    }

    suspend fun proxyImageLoading(url: String): Result<ByteArray> {
        return safeRequest { client.get(url).bodyAsChannel().toByteArray() }
    }

    private suspend inline fun <reified T> safeRequest(
        crossinline block: suspend () -> T,
    ): Result<T> {
        return try {
            val response = withContext(coroutineContext) { block() }
            Result.success(response)
        } catch (e: ClientRequestException) {
            Result.failure(e)
        } catch (e: ServerResponseException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
