package com.github.trueddd.data

import com.github.trueddd.data.items.BoardTrap
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days

data class GlobalState(
    val startDate: Date,
    // TODO: Add end date check for input actions
    val endDate: Date,
    val players: Map<Participant, PlayerState>,
    val boardLength: Int,
    val winner: Participant? = null,
    val gameGenreDistribution: GameGenreDistribution,
    val boardTraps: Map<Int, BoardTrap> = mapOf(),
) {

    operator fun get(playerName: String): PlayerState? {
        return players.entries.firstOrNull { (key, _) -> key.name == playerName }?.value
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

    fun updatePlayers(block: (Participant, PlayerState) -> PlayerState): GlobalState {
        return this.copy(players = players.mapValues { (player, playerState) ->
            block(player, playerState)
        })
    }

    companion object {

        const val START_POSITION = 0
        const val STINT_COUNT = 25
        val STINT_SIZE = Game.Genre.entries.size
        val PLAYABLE_BOARD_RANGE = 1 .. STINT_SIZE * STINT_COUNT

        fun default(
            genreDistribution: GameGenreDistribution = GameGenreDistribution.generateRandom(STINT_COUNT),
        ): GlobalState {
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
                boardLength = STINT_SIZE * STINT_COUNT,
                gameGenreDistribution = genreDistribution,
            )
        }
    }
}
