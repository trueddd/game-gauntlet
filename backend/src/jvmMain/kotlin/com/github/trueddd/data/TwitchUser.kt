package com.github.trueddd.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwitchUser(
    val id: String,
    val login: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
)
