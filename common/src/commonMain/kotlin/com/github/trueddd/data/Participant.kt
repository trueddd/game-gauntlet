package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    override val name: PlayerName,
    val displayName: String = name,
): Rollable {

    override val description: String
        get() = displayName

    override val color: Long
        get() = 0xFF000000 // TODO: use player-specific color
}
