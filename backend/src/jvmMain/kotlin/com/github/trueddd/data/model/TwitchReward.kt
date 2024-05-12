package com.github.trueddd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwitchReward(
    @SerialName("broadcaster_name")
    val broadcasterName: String,
    @SerialName("broadcaster_login")
    val broadcasterLogin: String,
    @SerialName("broadcaster_id")
    val broadcasterId: String,
    @SerialName("id")
    val id: String,
    @SerialName("image")
    val image: DefaultImage?,
    @SerialName("background_color")
    val backgroundColor: String,
    @SerialName("is_enabled")
    val isEnabled: Boolean,
    @SerialName("cost")
    val cost: Int,
    @SerialName("title")
    val title: String,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("is_user_input_required")
    val isUserInputRequired: Boolean,
    @SerialName("max_per_stream_setting")
    val maxPerStreamSetting: MaxPerStreamSetting,
    @SerialName("max_per_user_per_stream_setting")
    val maxPerUserPerStreamSetting: MaxPerUserPerStreamSetting,
    @SerialName("global_cooldown_setting")
    val globalCooldownSetting: GlobalCooldownSetting,
    @SerialName("is_paused")
    val isPaused: Boolean,
    @SerialName("is_in_stock")
    val isInStock: Boolean,
    @SerialName("default_image")
    val defaultImage: DefaultImage,
    @SerialName("should_redemptions_skip_request_queue")
    val shouldRedemptionsSkipRequestQueue: Boolean,
    @SerialName("redemptions_redeemed_current_stream")
    val redemptionsRedeemedCurrentStream: Int?,
    @SerialName("cooldown_expires_at")
    val cooldownExpiresAt: String?
)

@Serializable
data class MaxPerStreamSetting(
    @SerialName("is_enabled")
    val isEnabled: Boolean,
    @SerialName("max_per_stream")
    val maxPerStream: Int
)

@Serializable
data class MaxPerUserPerStreamSetting(
    @SerialName("is_enabled")
    val isEnabled: Boolean,
    @SerialName("max_per_user_per_stream")
    val maxPerUserPerStream: Int
)

@Serializable
data class GlobalCooldownSetting(
    @SerialName("is_enabled")
    val isEnabled: Boolean,
    @SerialName("global_cooldown_seconds")
    val globalCooldownSeconds: Int
)

@Serializable
data class DefaultImage(
    @SerialName("url_1x")
    val url1x: String,
    @SerialName("url_2x")
    val url2x: String,
    @SerialName("url_4x")
    val url4x: String
)
