package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Response {

    object ErrorCode {
        const val AuthError = "auth_error"
        const val TokenExpired = "token_expired"
    }

    companion object {

        private val encoder = Json {
            allowStructuredMapKeys = true
        }

        fun parse(input: String): Response? {
            return when {
                input.startsWith("s:") -> State(encoder.decodeFromString(input.removePrefix("s:")))
                input.startsWith("a:") -> UserAction(encoder.decodeFromString(input.removePrefix("a:")))
                input.startsWith("e:") -> Error(Exception(input.removePrefix("e:")))
                input.startsWith("i:") -> Info(input.removePrefix("i:"))
                else -> null
            }
        }
    }

    abstract val serialized: String

    data class State(val globalState: GlobalState) : Response() {
        override val serialized: String
            get() = "s:${encoder.encodeToString(globalState)}"
    }

    data class UserAction(val action: Action) : Response() {
        override val serialized: String
            get() = "a:${encoder.encodeToString(action)}"
    }

    data class Error(val exception: Exception) : Response() {
        override val serialized: String
            get() = "e:${exception.message}"
    }

    data class Info(val message: String) : Response() {
        override val serialized: String
            get() = "i:${message}"
    }
}
