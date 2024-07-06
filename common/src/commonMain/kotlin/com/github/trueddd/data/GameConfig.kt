package com.github.trueddd.data

import com.github.trueddd.map.Genre
import com.github.trueddd.map.MapConfig
import com.github.trueddd.map.RadioStation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameConfig(
    @SerialName("pl")
    val players: List<Participant>,
    @SerialName("sd")
    val startDate: Long,
    @SerialName("ed")
    val endDate: Long,
    @SerialName("mc")
    val mapConfig: MapConfig,
) : GameGenreDistribution, RadioCoverage {

    override val genres: List<Genre> by lazy {
        mapConfig.sectors.mapNotNull { it.genre }
    }

    override fun stationAt(position: Int): RadioStation {
        return checkNotNull(mapConfig.sectors[position].radio)
    }

    fun displayNameOf(playerName: PlayerName): String {
        return players.first { it.name == playerName }.displayName
    }
}
