package com.github.trueddd.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedTwitchUserData(
    val id: String,
    val playerName: String,
    val twitchToken: String,
    val rewardId: String?,
)
