package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class TwitchResponse<T>(
    val data: T,
)
