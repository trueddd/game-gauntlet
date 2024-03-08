package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.util.authRedirectUri
import com.github.trueddd.util.twitchClientId
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthManager(
    private val appClient: AppClient,
) {

    private companion object {
        const val STORAGE_KEY = "participant"
    }

    private val _userState: MutableStateFlow<Participant?>
    val userState: StateFlow<Participant?>
        get() = _userState.asStateFlow()

    init {
        val user = window.localStorage.getItem("participant")
            ?.let { Json.decodeFromString<Participant>(it) }
        _userState = MutableStateFlow(user)
    }

    private fun writeUser(participant: Participant?) {
        if (participant == null) {
            window.localStorage.removeItem(STORAGE_KEY)
        } else {
            window.localStorage.setItem("participant", Json.encodeToString(participant))
        }
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

    private fun removeHashFromLocation() {
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

    fun logout() {
        window.localStorage.removeItem("auth_state")
        window.localStorage.removeItem("participant")
        _userState.value = null
    }

    private suspend fun parseAuthResult(parameters: Map<String, String>): Result<Participant> {
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

    suspend fun auth(hashParameters: Map<String, String>): Result<Participant> {
        val participant = parseAuthResult(hashParameters).let { result ->
            if (result.isFailure) {
                return result
            } else {
                result.getOrThrow()
            }
        }
        writeUser(participant)
        if (window.location.hash.isNotEmpty()) {
            removeHashFromLocation()
        }
        return Result.success(participant)
    }
}
