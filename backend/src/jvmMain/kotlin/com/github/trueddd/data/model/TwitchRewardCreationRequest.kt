package com.github.trueddd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TwitchRewardCreationRequest(
    val title: String,
    val cost: Int,
//    val prompt: String?,
    @SerialName("background_color")
    val backgroundColor: String = "#9147FF",
)
