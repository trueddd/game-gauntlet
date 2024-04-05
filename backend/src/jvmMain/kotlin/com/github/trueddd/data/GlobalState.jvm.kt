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
    return GlobalState(
        startDate = startDateTimeInstant.toEpochMilliseconds(),
        endDate = (startDateTimeInstant + activePeriod).toEpochMilliseconds(),
        players = mapOf(
            Participant("truetripled") to PlayerState(),
            Participant("shizov") to PlayerState(),
            Participant("adash") to PlayerState(),
            Participant("superangerfetus") to PlayerState(),
        ),
        boardLength = GlobalState.STINT_SIZE * GlobalState.STINT_COUNT,
        gameGenreDistribution = genreDistribution,
    )
}
