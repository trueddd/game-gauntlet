package com.github.trueddd.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedConfig(
    val start: Long,
    val end: Long,
    val radio: String,
    val map: String,
    val points: Long,
)
