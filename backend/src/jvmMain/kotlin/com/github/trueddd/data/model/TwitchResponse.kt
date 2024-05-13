package com.github.trueddd.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TwitchResponse<T>(
    val data: T,
)
