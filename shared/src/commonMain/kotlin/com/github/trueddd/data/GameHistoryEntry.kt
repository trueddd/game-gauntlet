package com.github.trueddd.data

data class GameHistoryEntry(
    val game: Game,
    val status: Game.Status,
    val comment: String? = null,
)
