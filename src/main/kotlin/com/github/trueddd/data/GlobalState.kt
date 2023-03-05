package com.github.trueddd.data

import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days

data class GlobalState(
    val startDate: Date,
    val endDate: Date,
    val players: Map<Participant, PlayerState>,
    val boardLength: Int,
    val winner: Participant? = null,
) {

    operator fun get(playerName: String): PlayerState? {
        return players[Participant(playerName)]
    }

    fun updatePlayer(participant: Participant, block: (PlayerState) -> PlayerState): GlobalState {
        return this.copy(players = players.mapValues { (player, playerState) ->
            if (player == participant) {
                block(playerState)
            } else {
                playerState
            }
        })
    }

    companion object {

        private const val GENRES = 6
        private const val STINT_COUNT = 25

        fun default(): GlobalState {
            val startDate = Calendar.Builder().setDate(2022, 11, 15).build().time
            val endDate = Date(startDate.time + 21.days.inWholeMilliseconds)
            return GlobalState(
                startDate,
                endDate,
                players = mapOf(
                    Participant("shizov") to PlayerState(),
                    Participant("solll") to PlayerState(),
                    Participant("keli") to PlayerState(),
                ),
                boardLength = (GENRES + 1) * STINT_COUNT,
            )
        }
    }
}
