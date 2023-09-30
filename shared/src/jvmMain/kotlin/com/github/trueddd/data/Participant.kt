package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val name: String,
    val displayName: String = name,
    val link: String = "https://twitch.tv/$name",
)
