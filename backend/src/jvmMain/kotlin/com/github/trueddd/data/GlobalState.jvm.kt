package com.github.trueddd.data

import java.util.*
import kotlin.time.Duration.Companion.days

fun globalState(
    genreDistribution: GameGenreDistribution = GameGenreDistribution.generateRandom(GlobalState.STINT_COUNT),
): GlobalState {
    val startDate = Calendar.Builder().setDate(2022, 11, 15).build().timeInMillis
    val endDate = startDate + 21.days.inWholeMilliseconds
    return GlobalState(
        startDate,
        endDate,
        players = mapOf(
            Participant("megagamer") to PlayerState(),
            Participant("player") to PlayerState(),
            Participant("clutcher") to PlayerState(),
            Participant("dropper") to PlayerState(),
        ),
        boardLength = GlobalState.STINT_SIZE * GlobalState.STINT_COUNT,
        gameGenreDistribution = genreDistribution,
    )
}
