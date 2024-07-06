package com.github.trueddd.map

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MapConfig(
    val sectors: List<Sector>,
) {

    companion object {

        const val UNDEFINED_POSITION = -1f

        fun create(
            radioStationsSequence: String,
            genreSequence: String,
            positions: List<Pair<Float, Float>> = emptyList()
        ): MapConfig {
            require(radioStationsSequence.length == genreSequence.length)
            val size = radioStationsSequence.length + 1
            val serialization = Json
            return List(size) { index ->
                Sector(
                    index = index,
                    genre = if (index == 0) null else serialization.decodeFromString(Genre.serializer(), genreSequence[index - 1].toString()),
                    radio = if (index == 0) null else serialization.decodeFromString(RadioStation.serializer(), radioStationsSequence[index - 1].toString()),
                    position = positions.getOrElse(index) { UNDEFINED_POSITION to UNDEFINED_POSITION }
                        .let { (x, y) -> RelativePosition(x, y) },
                )
            }.let { MapConfig(it) }
        }

        fun generate(stintCount: Int, stintSize: Int): MapConfig {
            val size = stintSize * stintCount + 1

            val defaultGenres = Genre.entries - Genre.Special
            val genres = List(stintCount) { defaultGenres.shuffled() + Genre.Special }.flatten()

            val radioStationsOrder = RadioStation.entries.shuffled()

            val sectors = List(size) { index ->
                val stintIndex = (index - 1) / stintSize
                Sector(
                    index = index,
                    genre = if (index == 0) null else genres[index - 1],
                    radio = if (index == 0) null else radioStationsOrder[stintIndex % radioStationsOrder.size],
                    position = RelativePosition(UNDEFINED_POSITION, UNDEFINED_POSITION),
                )
            }
            return MapConfig(sectors)
        }
    }
}
