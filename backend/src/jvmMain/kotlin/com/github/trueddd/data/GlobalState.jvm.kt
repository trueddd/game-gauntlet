package com.github.trueddd.data

import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

fun globalState(
    genreDistribution: GameGenreDistribution = GameGenreDistribution.generateRandom(GlobalState.STINT_COUNT),
    startDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(DefaultTimeZone),
    activePeriod: Duration = 365.days,
): GlobalState {
    val startDateTimeInstant = startDateTime.toInstant(DefaultTimeZone)
    val players = listOf(
        Participant("truetripled"),
        Participant("shizov"),
        Participant("adash"),
        Participant("superangerfetus"),
    )
    return GlobalState(
        startDate = startDateTimeInstant.toEpochMilliseconds(),
        endDate = (startDateTimeInstant + activePeriod).toEpochMilliseconds(),
        players = players,
        gameGenreDistribution = genreDistribution,
        actions = emptyList(),
        stateSnapshot = StateSnapshot(
            playersState = players.associate { it.name to PlayerState.default() },
            boardTraps = emptyMap(),
            winner = null
        ),
        gameHistory = players.associate { it.name to emptyList() },
    )
}
