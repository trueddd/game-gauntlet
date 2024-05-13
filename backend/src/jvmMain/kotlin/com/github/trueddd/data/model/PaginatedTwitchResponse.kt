package com.github.trueddd.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedTwitchResponse<T>(
    val data: T,
    val pagination: Pagination?,
)

@Serializable
data class Pagination(val cursor: String? = null)
