package com.github.trueddd.map.default

import com.github.trueddd.map.MapConfig
import com.github.trueddd.map.RelativePosition
import com.github.trueddd.map.Sector

val GameMapConfig: MapConfig
    get() {
        val positions = getDefaultPositions()
        require(positions.size == MapFullLength) {
            "Amount of sectors must be equal to $MapFullLength, but was ${positions.size}"
        }
        val radioStations = getDefaultRadioStations()
        val radioCovered = List(MapConfig.LENGTH) { index -> radioStations.keys.any { index + 1 in it } }
        val uncovered = radioCovered.mapIndexedNotNull { index, covered ->
            if (!covered) index + 1 else null
        }
        require(uncovered.isEmpty()) {
            "Radio stations do not cover all sectors; uncovered: $uncovered"
        }
        val genres = getDefaultGenres()
        require(genres.size == MapFullLength - 1) {
            "Amount of genres must be equal to ${MapFullLength - 1}, but was ${genres.size}"
        }

        val sectors = List(MapFullLength) { index ->
            Sector(
                index = index,
                position = positions[index].let { (x, y) -> RelativePosition(x, y) },
                genre = if (index == 0) null else genres[index - 1],
                radio = if (index == 0) null else radioStations.firstNotNullOf { (range, radio) ->
                    if (index in range) radio else null
                },
            )
        }
        return MapConfig(sectors)
    }

private val MapFullLength: Int
    get() = MapConfig.STINT_SIZE * MapConfig.STINT_COUNT + 1
