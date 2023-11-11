package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class GameHistoryEntry(
    val game: Game,
    val status: Game.Status,
    val comment: String? = null,
)
