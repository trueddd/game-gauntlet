package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.util.authRedirectUri
import com.github.trueddd.util.twitchClientId
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthManager(
    private val appClient: AppClient,
) {

    var user: Participant?
        get() = window.localStorage.getItem("participant")
            ?.let { Json.decodeFromString<Participant>(it) }
        set(value) {
            window.localStorage.setItem("participant", value?.let { Json.encodeToString<Participant>(it) } ?: "")
        }

    fun receiveHashParameters(): Map<String, String> {
        val hash = window.location.hash.removePrefix("#")
        val arguments = hash.takeUnless { it.isEmpty() }
            ?.split("&")
            ?.map { it.split("=") }
            ?.associate { it[0] to it[1] }
            ?: emptyMap()
        println("arguments: $arguments")
        return arguments
    }

    fun removeHashFromLocation() {
        window.location.replace(
            URLBuilder(window.location.toString()).apply {
                fragment = ""
            }.buildString()
        )
    }

    fun requestAuth() {
        val state = Clock.System.now().hashCode().toString()
        window.localStorage.setItem("auth_state", state)
        val url = buildString {
            append("https://id.twitch.tv/oauth2/authorize")
            append("?response_type=token")
            append("&client_id=${twitchClientId()}")
            append("&redirect_uri=${authRedirectUri()}")
            append("&scope=user:read:email")
            append("&state=$state")
        }
        window.location.replace(Url(url).toString())
    }

    suspend fun parseAuthResult(parameters: Map<String, String>): Result<Participant> {
        if (!parameters.containsKey("state")) {
            return Result.failure(IllegalStateException("Response must contain state parameter"))
        }
        if (parameters["state"] != window.localStorage.getItem("auth_state")) {
            return Result.failure(IllegalStateException("State parameter does not match initial one"))
        }
        if (parameters.containsKey("error")) {
            return Result.failure(IllegalStateException(parameters["error_description"]))
        }
        val token = parameters["access_token"] ?: return Result.failure(IllegalStateException("No access token"))
        return appClient.verifyUser(token)
    }
}
