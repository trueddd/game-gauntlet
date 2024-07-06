package com.github.trueddd.map

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents sector on the map
 * @param index Sector index, starts with 0, where 0 is the starting sector
 * @param radio Radio station covering this sector
 * @param position Relative position of sector on the map
 */
@Serializable
data class Sector(
    @SerialName("i")
    val index: Int,
    @SerialName("g")
    val genre: Genre?,
    @SerialName("r")
    val radio: RadioStation?,
    @SerialName("p")
    val position: RelativePosition,
)
