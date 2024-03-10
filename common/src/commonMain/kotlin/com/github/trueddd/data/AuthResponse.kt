package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val user: Participant,
    val token: String,
)
