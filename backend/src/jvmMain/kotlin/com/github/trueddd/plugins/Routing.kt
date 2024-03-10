package com.github.trueddd.plugins

import com.github.trueddd.core.EventGate
import com.github.trueddd.core.GameLoader
import com.github.trueddd.core.HttpClient
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
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val eventGate by inject<EventGate>()
    val httpClient by inject<HttpClient>()

    routing {
        install(CachingHeaders) {
            options { _, _ ->
                CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60))
            }
        }

        staticResources("/icons", "icons/items")

        authenticate {
            get("/actions") {
                call.caching = CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Public))
                call.respond(eventGate.historyHolder.getActions())
            }
        }

        post("/game") {
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

        get("items") {
            call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
            val items = getItemFactoriesSet().map { it.create() }
            call.respond(items)
        }

        post("user") {
            call.caching = CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Public))
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
