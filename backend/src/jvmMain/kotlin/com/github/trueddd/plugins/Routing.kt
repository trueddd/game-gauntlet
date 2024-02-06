package com.github.trueddd.plugins

import com.github.trueddd.data.request.DownloadGameRequestBody
import com.github.trueddd.di.getItemFactoriesSet
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.createTorrentClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.nio.file.Files
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Application.configureRouting() {
    routing {
        install(CachingHeaders) {
            options { _, _ ->
                CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60))
            }
        }

        staticFiles("/icons", File("src/jvmMain/resources/icons/items"))

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
            val currentDir = Environment.GamesDirectory
                .resolve(call.request.hashCode().toString())
                .also { it.mkdir() }
            val client = createTorrentClient(fileToLoad, currentDir)
            suspendCoroutine { con ->
                client.startAsync({
                    if (it.piecesRemaining == 0) {
                        client.stop()
                        con.resume(Unit)
                    }
                }, 1000L).join()
            }
            val downloadedFile = currentDir.walk()
                .filter { it.isFile }
                .filter { it.nameWithoutExtension.equals(fileToLoad, ignoreCase = true) }
                .firstOrNull() ?: run {
                call.respond(HttpStatusCode.NotFound, "No game was found with name `$fileToLoad`")
                return@post
            }
            Files.copy(downloadedFile.toPath(), Environment.GamesDirectory.resolve(downloadedFile.name).toPath())
            currentDir.deleteRecursively()
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName, downloadedFile.name)
                    .toString()
            )
            call.respondFile(Environment.GamesDirectory.resolve(downloadedFile.name))
            if (call.response.isSent) {
                Environment.GamesDirectory.resolve(downloadedFile.name).delete()
            }
        }

        get("items") {
            call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
            val items = getItemFactoriesSet().map { it.create() }
            call.respond(items)
        }
    }
}
