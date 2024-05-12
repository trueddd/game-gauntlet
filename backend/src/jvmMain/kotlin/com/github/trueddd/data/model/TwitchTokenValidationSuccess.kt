package com.github.trueddd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TwitchTokenValidationSuccess(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("login")
    val login: String,
    @SerialName("scopes")
    val scopes: List<String>,
    @SerialName("expires_in")
    val expiresIn: Int,
)
