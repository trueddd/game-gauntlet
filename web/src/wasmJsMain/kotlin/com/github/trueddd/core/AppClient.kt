package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.*
import com.github.trueddd.items.WheelItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppClient(
    private val httpClient: HttpClient,
    private val router: ServerRouter,
) : CoroutineScope {

    override val coroutineContext by lazy {
        Dispatchers.Default + SupervisorJob()
    }

    private fun savedJwtToken(): String? {
        return window.localStorage.getItem(AuthManager.TOKEN_KEY)
    }

    fun getActionsFlow(): Flow<List<Action>> {
        return callbackFlow {
            send(loadActions())
            val session = httpClient.webSocketSession(router.ws(Router.ACTIONS))
            val wsJob = launch {
                for (frame in session.incoming) {
                    if (!isActive) break
                    val textFrame = frame as? Frame.Text ?: continue
                    val content = textFrame.readText()
                    println("New action received: $content")
                    val data = Response.parse(textFrame.readText()) ?: continue
                    if (data is Response.UserAction) {
                        this@callbackFlow.send(listOf(data.action))
                    }
                }
            }
            awaitClose {
                wsJob.cancel()
                session.cancel()
            }
        }
    }

    fun getPlayersHistoryFlow(): Flow<PlayersHistory> {
        return callbackFlow {
            val session = httpClient.webSocketSession(router.ws(Router.TURNS))
            val wsJob = launch {
                val token = savedJwtToken() ?: run {
                    this@callbackFlow.cancel()
                    return@launch
                }
                session.outgoing.send(Frame.Text(token))
                for (frame in session.incoming) {
                    if (!isActive) break
                    val textFrame = frame as? Frame.Text ?: continue
                    val data = Response.parse(textFrame.readText()) ?: continue
                    if (data is Response.Turns) {
                        this@callbackFlow.send(data.playersHistory)
                    }
                }
            }
            awaitClose {
                wsJob.cancel()
                session.cancel()
            }
        }
    }

    private suspend fun loadActions(): List<Action> =
        getJsonData(router.http(Router.ACTIONS), sendBearerToken = true) ?: emptyList()

    suspend fun loadImage(url: String): ByteArray? {
        return withContext(coroutineContext) {
            try {
                httpClient.get(url) {
                    contentType(ContentType.Image.PNG)
                }.bodyAsChannel().toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun verifyUser(token: String): Result<AuthResponse> {
        return withContext(coroutineContext) {
            try {
                httpClient.post(router.http(Router.USER)) {
                    contentType(ContentType.Application.Json)
                    parameter("token", token)
                }.body<AuthResponse>().let { Result.success(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun checkTwitchRewardAvailability(token: String): Result<Unit> {
        return withContext(coroutineContext) {
            try {
                val response = httpClient.get(router.http(Router.REWARD)) {
                    bearerAuth(token)
                }
                if (response.status == HttpStatusCode.OK) {
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalStateException())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun createTwitchReward(token: String): Result<Unit> {
        return withContext(coroutineContext) {
            try {
                val response = httpClient.post(router.http(Router.REWARD)) {
                    bearerAuth(token)
                }
                if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalStateException())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun getGameConfig(): GameConfig? = getJsonData(router.http(Router.CONFIG))

    suspend fun getStateSnapshot(): StateSnapshot? = getJsonData(router.http(Router.SNAPSHOT))

    suspend fun getPlayersHistory(): PlayersHistory? =
        getJsonData(router.http(Router.TURNS), sendBearerToken = true)

    suspend fun getGames(genre: Game.Genre): List<Game> =
        getJsonData<List<Game>>(router.http(Router.Wheels.GAMES), sendBearerToken = true)
            ?.filter { it.genre == genre } ?: emptyList()

    suspend fun rollItem(): WheelItem? =
        getJsonData(router.http(Router.Wheels.ROLL_ITEMS), sendBearerToken = true)

    suspend fun rollGame(genre: Game.Genre): Game? = withContext(coroutineContext) {
        try {
            httpClient.get(router.http(Router.Wheels.ROLL_GAMES)) {
                parameter("genre", genre.ordinal)
                savedJwtToken()?.let { bearerAuth(it) }
                contentType(ContentType.Application.Json)
            }.body<Game>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun rollPlayer(): Participant? =
        getJsonData(router.http(Router.Wheels.ROLL_PLAYERS), sendBearerToken = true)

    private suspend inline fun <reified T> getJsonData(
        urlString: String,
        sendBearerToken: Boolean = false
    ): T? {
        return withContext(coroutineContext) {
            try {
                httpClient.get(urlString) {
                    if (sendBearerToken) {
                        savedJwtToken()?.let { bearerAuth(it) }
                    }
                    contentType(ContentType.Application.Json)
                }.body<T>()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
