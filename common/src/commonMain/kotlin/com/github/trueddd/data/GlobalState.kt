package com.github.trueddd.data

import com.github.trueddd.actions.Action
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Contains all information about current game state.
 * But not everything should be delivered to frontend as single package.
 */
@Serializable
data class GlobalState(
    @SerialName("ac")
    val actions: List<Action>,
    @SerialName("pl")
    val players: List<Participant>,
    @SerialName("ss")
    val stateSnapshot: StateSnapshot,
    @SerialName("gh")
    val gameHistory: Map<PlayerName, List<GameHistoryEntry>>,
    @SerialName("gd")
    val gameGenreDistribution: GameGenreDistribution,
    @SerialName("sd")
    val startDate: Long,
    @SerialName("ed")
    val endDate: Long,
    @SerialName("rc")
    val radioCoverage: RadioCoverage,
) {

    companion object {
        const val START_POSITION = 0
        const val STINT_COUNT = 25
        val STINT_SIZE = Game.Genre.entries.size
        val PLAYABLE_BOARD_RANGE = 1..STINT_SIZE * STINT_COUNT
    }

    val boardLength: Int
        get() = STINT_SIZE * STINT_COUNT

    val gameConfig: GameConfig
        get() = GameConfig(players, gameGenreDistribution, startDate, endDate, radioCoverage)

    fun stateOf(participant: Participant) = stateSnapshot.playersState[participant.name]!!
    fun effectsOf(participant: Participant) = stateOf(participant).effects
    fun pendingEventsOf(participant: Participant) = stateOf(participant).pendingEvents
    fun inventoryOf(participant: Participant) = stateOf(participant).inventory
    fun positionOf(participant: Participant) = stateOf(participant).position
    fun gamesOf(participant: Participant) = gameHistory[participant.name]!!

    fun getAllEverRolledGames(): List<Game> {
        return gameHistory.flatMap { (_, games) -> games.map(GameHistoryEntry::game) }
    }

    fun getDroppedGames(): List<Game.Id> {
        return gameHistory.flatMap { (_, games) ->
            games.filter { it.status == Game.Status.Dropped }
                .map { it.game.id }
        }
    }

    fun participantByName(name: String): Participant? {
        return players.firstOrNull { it.name == name }
    }

    fun positionAmongPlayers(player: Participant): Int {
        val positions = stateSnapshot.playersState.values.map { it.position }.distinct().sortedDescending()
        return positions.indexOf(positionOf(player))
    }

    fun updateCurrentGame(participant: Participant): GlobalState {
        return updatePlayer(participant) { state ->
            state.copy(
                currentGame = gameHistory[participant.name]?.lastOrNull { it.status != Game.Status.Next }
            )
        }
    }

    fun updateGameHistory(
        participant: Participant,
        block: (List<GameHistoryEntry>) -> List<GameHistoryEntry>
    ): GlobalState {
        return this.copy(gameHistory = gameHistory.mapValues { (playerName, history) ->
            if (playerName == participant.name) {
                block(history)
            } else {
                history
            }
        })
    }

    fun updatePlayer(participant: Participant, block: (PlayerState) -> PlayerState): GlobalState {
        return this.copy(stateSnapshot = stateSnapshot.copy(
            playersState = stateSnapshot.playersState.mapValues { (player, playerState) ->
                if (player == participant.name) {
                    block(playerState)
                } else {
                    playerState
                }
            }
        ))
    }

    fun updatePlayers(block: (Participant, PlayerState) -> PlayerState): GlobalState {
        return this.copy(stateSnapshot = stateSnapshot.copy(
            playersState = stateSnapshot.playersState.mapValues { (playerName, state) ->
                block(participantByName(playerName)!!, state)
            }
        ))
    }

    fun defaultPlayersHistory() = players.associate { it.name to PlayerTurnsHistory.default() }
}
