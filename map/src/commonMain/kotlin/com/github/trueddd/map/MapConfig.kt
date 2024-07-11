package com.github.trueddd.map

import kotlinx.serialization.Serializable

@Serializable
data class MapConfig(
    val sectors: List<Sector>,
) {

    companion object {

        const val STINT_COUNT = 25

        val STINT_SIZE = Genre.entries.size

        val LENGTH: Int = STINT_SIZE * STINT_COUNT
    }
}
