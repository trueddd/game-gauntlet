package com.github.trueddd.core

import com.github.trueddd.data.AuthResponse
import com.github.trueddd.data.Participant
import com.github.trueddd.util.authRedirectUri
import com.github.trueddd.util.twitchClientId
import com.github.trueddd.utils.serialization
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString

class AuthManager(
    private val appClient: AppClient,
) {

    companion object {
        const val USER_KEY = "participant"
        const val TOKEN_KEY = "token"
        const val STATE_KEY = "auth_state"
    }

    private val _userState: MutableStateFlow<Participant?>
    val userState: StateFlow<Participant?>
        get() = _userState.asStateFlow()

    val isAuthorized: Boolean
        get() = _userState.value != null

    init {
        val user = window.localStorage.getItem(USER_KEY)
            ?.let { serialization.decodeFromString<Participant>(it) }
        _userState = MutableStateFlow(user)
    }

    private fun writeUser(authResponse: AuthResponse) {
        window.localStorage.setItem(USER_KEY, serialization.encodeToString(authResponse.user))
        window.localStorage.setItem(TOKEN_KEY, authResponse.token)
    }

    fun savedJwtToken(): String? {
        return window.localStorage.getItem(TOKEN_KEY)
    }

    fun receiveHashParameters(): Map<String, String> {
        val hash = window.location.hash.removePrefix("#")
        val arguments = hash.takeUnless { it.isEmpty() }
            ?.split("&")
            ?.map { it.split("=") }
            ?.associate { it[0] to it[1] }
            ?: emptyMap()
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
        window.localStorage.setItem(STATE_KEY, state)
        val scopes = listOf(
            "user:read:email",
            "channel:read:redemptions",
            "channel:manage:redemptions",
        ).joinToString(" ")
        val url = buildString {
            append("https://id.twitch.tv/oauth2/authorize")
            append("?response_type=token")
            append("&client_id=${twitchClientId()}")
            append("&redirect_uri=${authRedirectUri()}")
            append("&scope=$scopes")
            append("&state=$state")
        }
        window.location.replace(Url(url).toString())
    }

    fun logout() {
        window.localStorage.removeItem(STATE_KEY)
        window.localStorage.removeItem(USER_KEY)
        window.localStorage.removeItem(TOKEN_KEY)
        _userState.value = null
    }

    private suspend fun parseAuthResult(parameters: Map<String, String>): Result<AuthResponse> {
        if (!parameters.containsKey("state")) {
            return Result.failure(IllegalStateException("Response must contain state parameter"))
        }
        if (parameters["state"] != window.localStorage.getItem(STATE_KEY)) {
            return Result.failure(IllegalStateException("State parameter does not match initial one"))
        }
        if (parameters.containsKey("error")) {
            return Result.failure(IllegalStateException(parameters["error_description"]))
        }
        val token = parameters["access_token"] ?: return Result.failure(IllegalStateException("No access token"))
        return appClient.verifyUser(token)
    }

    suspend fun auth(hashParameters: Map<String, String>): Result<Participant> {
        val authResponse = parseAuthResult(hashParameters).let { result ->
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull()!!)
            } else {
                result.getOrThrow()
            }
        }
        writeUser(authResponse)
        if (window.location.hash.isNotEmpty()) {
            removeHashFromLocation()
        }
        return Result.success(authResponse.user)
    }
}
