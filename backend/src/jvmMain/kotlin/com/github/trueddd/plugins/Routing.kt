package com.github.trueddd.plugins

import com.github.trueddd.core.EventGate
import com.github.trueddd.core.GamesProvider
import com.github.trueddd.core.HttpClient
import com.github.trueddd.core.ItemRoller
import com.github.trueddd.core.Router
import com.github.trueddd.data.AuthResponse
import com.github.trueddd.data.repository.TwitchUsersRepository
import com.github.trueddd.map.Genre
import com.github.trueddd.utils.Environment
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.cachingheaders.caching
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.flow.firstOrNull
import org.koin.ktor.ext.inject

@Suppress("CyclomaticComplexMethod")
fun Application.configureRouting() {
    val eventGate by inject<EventGate>()
    val httpClient by inject<HttpClient>()
    val itemRoller by inject<ItemRoller>()
    val gamesProvider by inject<GamesProvider>()
    val twitchUsersRepository by inject<TwitchUsersRepository>()

    routing {
        install(CachingHeaders)

        staticResources(Router.ICONS, "icons/items") {
            cacheControl { listOf(CacheControl.MaxAge(maxAgeSeconds = 3600)) }
        }

        // Called once user opens web app
        get(Router.CONFIG) {
            cache()
            call.respond(eventGate.stateHolder.current.gameConfig)
        }

        get(Router.SNAPSHOT) {
            cache()
            call.respond(eventGate.stateHolder.current.stateSnapshot)
        }

        post(Router.USER) {
            val userToken = call.parameters["token"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "No access token passed")
                return@post
            }
            val twitchUser = httpClient.getTwitchUser(userToken).getOrNull() ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            val participant = eventGate.stateHolder.participants
                .firstOrNull { player -> player.name == twitchUser.login }
                ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
            twitchUsersRepository.saveUser(twitchUser.id, twitchUser.login, userToken)
            val token = createJwtToken(twitchUser.login)
            call.respond(HttpStatusCode.OK, AuthResponse(participant, token))
        }

        get(Router.REMOTE) {
            cache()
            val player = call.parameters["player"]?.let { name ->
                eventGate.stateHolder.participants.firstOrNull { it.name == name }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest, "No player name passed")
                return@get
            }
            val data = httpClient.proxyImageLoading(player.backgroundUrl)
            if (data.isSuccess) {
                call.respondBytes(data.getOrThrow(), ContentType.Image.Any)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        authenticate {
            get(Router.ACTIONS) {
                call.respond(eventGate.historyHolder.getActions())
            }
            get(Router.Wheels.GAMES) {
                cache()
                val items = gamesProvider.listAll()
                call.respond(items)
            }
            get(Router.Wheels.ROLL_ITEMS) {
                call.respond(itemRoller.pick())
            }
            get(Router.Wheels.ROLL_GAMES) {
                val genre = call.parameters["genre"]?.toIntOrNull()?.let { raw -> Genre.entries.getOrNull(raw) }
                call.respond(gamesProvider.roll(genre))
            }
            get(Router.Wheels.ROLL_PLAYERS) {
                val user = call.userLogin
                call.respond(eventGate.stateHolder.participants.filter { player -> player.name != user }.random())
            }
            get(Router.REWARD) {
                val requester = call.userLogin
                val hasReward = twitchUsersRepository.getUsersFlow()
                    .firstOrNull { user -> user.playerName == requester }
                    ?.rewardId != null
                call.respond(if (hasReward) HttpStatusCode.OK else HttpStatusCode.NotFound)
            }
            post(Router.REWARD) {
                val requester = call.userLogin
                val twitchUser = twitchUsersRepository.getUsersFlow()
                    .firstOrNull { user -> user.playerName == requester }
                    ?: run {
                        call.respond(HttpStatusCode.NotFound)
                        return@post
                    }
                val hasReward = twitchUser.rewardId != null
                if (hasReward) {
                    call.respond(HttpStatusCode.OK)
                    return@post
                }
                val reward = httpClient.createTwitchReward(twitchUser.id, twitchUser.twitchToken).getOrNull() ?: run {
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }
                twitchUsersRepository.updateReward(twitchUser.playerName, reward.id)
                call.respond(HttpStatusCode.Created)
            }
        }

        // Profile scope
        get(Router.TURNS) {
            if (call.userLogin == null) {
                cache()
            }
            call.respond(eventGate.stateHolder.currentPlayersHistory)
        }
    }
}

fun PipelineContext<Unit, ApplicationCall>.cache() {
    call.caching = if (Environment.IsDev) {
        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    } else {
        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 600))
    }
}
