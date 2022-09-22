package com.github.trueddd.data

import java.util.Date
import kotlin.time.Duration.Companion.days

data class GlobalState(
    val startDate: Date,
    val endDate: Date,
    val players: Map<Participant, PlayerState>,
) {

    companion object {
        fun default(): GlobalState {
            val startDate = Date()
            val endDate = Date(startDate.time + 14.days.inWholeMilliseconds)
            return GlobalState(
                startDate,
                endDate,
                mapOf(
                    Participant("shizov") to PlayerState(),
                    Participant("solll") to PlayerState(),
                    Participant("keli") to PlayerState(),
                ),
            )
        }
    }
}
