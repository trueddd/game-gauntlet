package com.github.trueddd.map

import kotlinx.serialization.Serializable

/**
 * Represents relative position of sector on the map
 * @param x X coordinate, value in range [0..1]
 * @param y Y coordinate, value in range [0..1]
 */
@Serializable
data class RelativePosition(
    val x: Float,
    val y: Float,
)
