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

    fun stateOf(participant: Participant) = get(participant.name)!!
    fun effectsOf(participant: Participant) = stateOf(participant).effects
    fun pendingEventsOf(participant: Participant) = stateOf(participant).pendingEvents
    fun inventoryOf(participant: Participant) = stateOf(participant).inventory
    fun positionOf(participant: Participant) = stateOf(participant).position

    fun getAllEverRolledGames(): List<Game> {
        return players.values
            .flatMap { it.gameHistory }
            .map { it.game }
    }

    fun getDroppedGames(): List<Game.Id> {
        return players.values.flatMap { playerState ->
            playerState.gameHistory
                .filter { it.status == Game.Status.Dropped }
                .map { it.game.id }
        }
    }

    fun participantByName(name: String): Participant? {
        return players.keys.firstOrNull { it.name == name }
    }

    fun positionAmongPlayers(player: Participant): Int {
        val positions = players.values.map { it.position }.distinct().sortedDescending()
        return positions.indexOf(positionOf(player))
    }

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
                    Participant("megagamer") to PlayerState(),
                    Participant("player") to PlayerState(),
                    Participant("clutcher") to PlayerState(),
                    Participant("dropper") to PlayerState(),
                ),
                boardLength = STINT_SIZE * STINT_COUNT,
                gameGenreDistribution = genreDistribution,
            )
        }
    }
}
