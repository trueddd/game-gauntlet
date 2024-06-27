package com.github.trueddd.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameConfig(
    @SerialName("pl")
    val players: List<Participant>,
    @SerialName("gd")
    val gameGenreDistribution: GameGenreDistribution,
    @SerialName("sd")
    val startDate: Long,
    @SerialName("ed")
    val endDate: Long,
    @SerialName("rc")
    val radioCoverage: RadioCoverage,
) {

    fun displayNameOf(playerName: PlayerName): String {
        return players.first { it.name == playerName }.displayName
    }
}
