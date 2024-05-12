package com.github.trueddd.plugins

import com.github.trueddd.core.*
import com.github.trueddd.data.AuthResponse
import com.github.trueddd.data.Game
import com.github.trueddd.data.repository.TwitchUsersRepository
import com.github.trueddd.utils.Environment
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject

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
            val participant = eventGate.stateHolder.participants.firstOrNull { it.name == twitchUser.login } ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            twitchUsersRepository.saveUser(twitchUser.id, twitchUser.login, userToken)
            val token = createJwtToken(twitchUser.login)
            call.respond(HttpStatusCode.OK, AuthResponse(participant, token))
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
                val genre = call.parameters["genre"]?.toIntOrNull()?.let { Game.Genre.entries.getOrNull(it) }
                call.respond(gamesProvider.roll(genre))
            }
            get(Router.Wheels.ROLL_PLAYERS) {
                val user = call.userLogin!!
                call.respond(eventGate.stateHolder.participants.filter { it.name != user }.random())
            }
            get(Router.REWARD) {
                val user = call.userLogin!!
                val hasReward = twitchUsersRepository.getUsers().firstOrNull { it.playerName == user }?.rewardId != null
                call.respond(if (hasReward) HttpStatusCode.OK else HttpStatusCode.NotFound)
            }
            post(Router.REWARD) {
                val user = call.userLogin!!
                val twitchUser = twitchUsersRepository.getUsers().firstOrNull { it.playerName == user } ?: run {
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
