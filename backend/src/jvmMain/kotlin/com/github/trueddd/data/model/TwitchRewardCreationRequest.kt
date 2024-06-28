package com.github.trueddd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TwitchRewardCreationRequest(
    @SerialName("title")
    val title: String,
    @SerialName("cost")
    val cost: Int,
    @SerialName("background_color")
    val backgroundColor: String = "#9147FF",
)
