package com.github.trueddd.core

import com.github.trueddd.data.Participant

data class AppState(
    val user: Participant?,
) {
    companion object {
        fun default() = AppState(
            user = null,
        )
    }
}
