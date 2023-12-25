package com.github.trueddd.data.request

import kotlinx.serialization.Serializable

@Serializable
data class DownloadGameRequestBody(
    val name: String,
)
