package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    override val name: String,
    val displayName: String = name,
    val link: String = "https://twitch.tv/$name",
): Rollable {

    override val description: String
        get() = displayName

    override val color: Long
        get() = 0xFF000000 // TODO: use player-specific color
}
