package com.github.trueddd.data

import com.github.trueddd.map.default.GameMapConfig
import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

fun globalState(
    startDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(DefaultTimeZone),
    activePeriod: Duration = 365.days,
    raisedAmountOfPoints: Long = 0L,
): GlobalState {
    val startDateTimeInstant = startDateTime.toInstant(DefaultTimeZone)
    val players = listOf(
        Participant.Truetripled,
        Participant.Shizov,
        Participant.Adash,
        Participant.ChilloutLatte,
    )
    return GlobalState(
        startDate = startDateTimeInstant.toEpochMilliseconds(),
        endDate = (startDateTimeInstant + activePeriod).toEpochMilliseconds(),
        players = players,
        actions = emptyList(),
        stateSnapshot = StateSnapshot(
            playersState = players.associate { it.name to PlayerState.default() },
            boardTraps = emptyMap(),
            winner = null,
            overallAmountOfPointsRaised = raisedAmountOfPoints,
            scheduledEvent = null,
        ),
        gameHistory = players.associate { it.name to emptyList() },
        mapConfig = GameMapConfig,
    )
}
