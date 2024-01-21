package com.github.trueddd

import com.github.trueddd.data.request.DownloadGameRequestBody
import com.github.trueddd.utils.WebEnvironment
import dev.fritz2.core.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

private val client by lazy {
    HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
    }
}

private fun ByteArray.toBlob(): Blob {
    return Blob(arrayOf(this), BlobPropertyBag(ContentType.Application.OctetStream.toString()))
}

private suspend fun searchGame(name: String) {
    try {
        val response = client.post("http://localhost:${WebEnvironment.ServerPort}/game") {
            contentType(ContentType.Application.Json)
            setBody(DownloadGameRequestBody(name))
            onDownload { bytesSentTotal, contentLength ->
                console.log("Received $bytesSentTotal bytes from $contentLength")
            }
        }
        val body = response.body<ByteArray>()
        val link = document.createElement("a") as HTMLAnchorElement
        link.setAttribute("href", URL.createObjectURL(body.toBlob()))
        link.setAttribute("download", "$name.exe")
        link.click()
    } catch (e: Exception) {
        e.printStackTrace()
        console.error(e)
    }
}

fun RenderContext.renderArchives() {
    val isLoadingStateFlow = MutableStateFlow(false)
    val gameName = storeOf("")
    val shouldBlockFlow = combine(gameName.data, isLoadingStateFlow) { name, isLoading -> name.isBlank() || isLoading }
    label("block mb-2 text-sm font-medium text-gray-100") {
        + "Game"
        `for`(gameName.id)
    }
    input(
        id = gameName.id,
        baseClass = "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
    ) {
        type("text")
        placeholder("Game name, e.g. \"Эволи\"")
        disabled(isLoadingStateFlow)
        changes.values() handledBy gameName.update
    }
    button("text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800") {
        +"Search"
        disabled(shouldBlockFlow)
        clicks handledBy {
            isLoadingStateFlow.value = true
            searchGame(gameName.current)
            isLoadingStateFlow.value = false
        }
    }
}
