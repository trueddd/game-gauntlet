package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.PlayersHistory
import com.github.trueddd.data.StateSnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Response {

    @Suppress("ConstPropertyName")
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
                input.startsWith("t:") -> Turns(encoder.decodeFromString(input.removePrefix("t:")))
                input.startsWith("a:") -> UserAction(encoder.decodeFromString(input.removePrefix("a:")))
                input.startsWith("e:") -> Error(Exception(input.removePrefix("e:")))
                input.startsWith("i:") -> Info(input.removePrefix("i:"))
                else -> null
            }
        }
    }

    abstract val serialized: String

    data class State(val snapshot: StateSnapshot) : Response() {
        override val serialized: String
            get() = "s:${encoder.encodeToString(snapshot)}"
    }

    data class Turns(val playersHistory: PlayersHistory) : Response() {
        override val serialized: String
            get() = "t:${encoder.encodeToString(playersHistory)}"
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
