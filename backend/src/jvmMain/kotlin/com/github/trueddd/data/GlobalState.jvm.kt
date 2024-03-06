package com.github.trueddd.data

import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

fun globalState(
    genreDistribution: GameGenreDistribution = GameGenreDistribution.generateRandom(GlobalState.STINT_COUNT),
    startDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(DefaultTimeZone),
    activePeriod: Duration = 21.days,
): GlobalState {
    val startDateTimeInstant = startDateTime.toInstant(DefaultTimeZone)
    return GlobalState(
        startDate = startDateTimeInstant.toEpochMilliseconds(),
        endDate = (startDateTimeInstant + activePeriod).toEpochMilliseconds(),
        players = mapOf(
            Participant("truetripled") to PlayerState(),
            Participant("player") to PlayerState(),
            Participant("clutcher") to PlayerState(),
            Participant("dropper") to PlayerState(),
        ),
        boardLength = GlobalState.STINT_SIZE * GlobalState.STINT_COUNT,
        gameGenreDistribution = genreDistribution,
    )
}
