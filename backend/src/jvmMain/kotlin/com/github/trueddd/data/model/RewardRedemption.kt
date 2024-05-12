package com.github.trueddd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewardRedemption(
    @SerialName("broadcaster_name")
    val broadcasterName: String,
    @SerialName("broadcaster_login")
    val broadcasterLogin: String,
    @SerialName("broadcaster_id")
    val broadcasterId: String,
    @SerialName("id")
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_name")
    val userName: String,
    @SerialName("redeemed_at")
    val redeemedAt: String,
    @SerialName("status")
    val status: String,
    @SerialName("reward")
    val reward: Reward,
)
