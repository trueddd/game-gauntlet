package com.github.trueddd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwitchUser(
    @SerialName("id")
    val id: String,
    @SerialName("login")
    val login: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
)
