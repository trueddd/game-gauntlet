package com.github.trueddd.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameHistoryEntry(
    @SerialName("gm")
    val game: Game,
    @SerialName("st")
    val status: Game.Status,
    @SerialName("co")
    val comment: String? = null,
)
