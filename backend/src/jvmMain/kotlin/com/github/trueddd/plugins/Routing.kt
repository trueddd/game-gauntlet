package com.github.trueddd.plugins

import com.github.trueddd.core.*
import com.github.trueddd.data.AuthResponse
import com.github.trueddd.data.request.DownloadGameRequestBody
import com.github.trueddd.di.getItemFactoriesSet
import com.github.trueddd.utils.Environment
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val eventGate by inject<EventGate>()
    val httpClient by inject<HttpClient>()
    val itemRoller by inject<ItemRoller>()
    val gamesProvider by inject<GamesProvider>()

    routing {
        install(CachingHeaders)

        staticResources(Router.ICONS, "icons/items") {
            cacheControl { listOf(CacheControl.MaxAge(maxAgeSeconds = 3600)) }
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
            get(Router.Wheels.PLAYERS) {
                cache()
                val user = call.userLogin!!
                val items = eventGate.stateHolder.participants.filter { it.name != user }
                call.respond(items)
            }
            get(Router.Wheels.ROLL_ITEMS) {
                call.respond(itemRoller.pick())
            }
            get(Router.Wheels.ROLL_GAMES) {
                call.respond(gamesProvider.roll())
            }
            get(Router.Wheels.ROLL_PLAYERS) {
                val user = call.userLogin!!
                call.respond(eventGate.stateHolder.participants.filter { it.name != user }.random())
            }
        }

        post(Router.LOAD_GAME) {
            val fileToLoad = try {
                call.receive<DownloadGameRequestBody>().name
            } catch (e: Exception) {
                if (Environment.IsDev) {
                    "Фишдом. Время праздников"
                } else {
                    throw e
                }
            }
            val gameLoader = this@routing.get<GameLoader>()
            val downloadedFile = gameLoader.loadGame(fileToLoad) ?: run {
                call.respond(HttpStatusCode.NotFound, "No game was found with name `$fileToLoad`")
                return@post
            }
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName, downloadedFile.name)
                    .toString()
            )
            call.respondFile(downloadedFile)
            if (call.response.isSent) {
                downloadedFile.delete()
            }
        }

        get(Router.Wheels.ITEMS) {
            cache()
            val items = getItemFactoriesSet().map { it.create() }
            call.respond(items)
        }

        post(Router.USER) {
            val userToken = call.parameters["token"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "No access token passed")
                return@post
            }
            val twitchUser = httpClient.getTwitchUser(userToken).getOrNull()
                ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
            val participant = eventGate.stateHolder.participants
                .firstOrNull { it.name == twitchUser.login }
                ?: run {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
            val token = createJwtToken(twitchUser.login)
            call.respond(HttpStatusCode.OK, AuthResponse(participant, token))
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
