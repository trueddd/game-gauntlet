package com.github.trueddd.data.model.save

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GameConfig(
    @SerialName("start_time")
    val startTime: Long,
    @SerialName("end_time")
    val endTime: Long,
    @SerialName("points_collected")
    val pointsCollected: Long,
)
