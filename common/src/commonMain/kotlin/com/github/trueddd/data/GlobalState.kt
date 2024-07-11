package com.github.trueddd.data

import com.github.trueddd.actions.Action
import com.github.trueddd.map.Genre
import com.github.trueddd.map.MapConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

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
    @SerialName("sd")
    val startDate: Long,
    @SerialName("ed")
    val endDate: Long,
    @SerialName("mc")
    val mapConfig: MapConfig,
) : GameGenreDistribution {

    companion object {
        const val START_POSITION = 0
        const val STINT_COUNT = 25
        val STINT_SIZE = Genre.entries.size
        val PLAYABLE_BOARD_RANGE = 1..STINT_SIZE * STINT_COUNT
        val BOARD_RANGE = 0..STINT_SIZE * STINT_COUNT
    }

    override val genres: List<Genre> by lazy {
        mapConfig.sectors.mapNotNull { it.genre }
    }

    /**
     * Playable board length (without start/zero position)
     */
    val boardLength: Int
        get() = mapConfig.sectors.size - 1

    val gameConfig: GameConfig
        get() = GameConfig(players, startDate, endDate, mapConfig)

    fun stateOf(playerName: PlayerName) = stateSnapshot.playersState[playerName]!!
    fun effectsOf(playerName: PlayerName) = stateOf(playerName).effects
    fun pendingEventsOf(playerName: PlayerName) = stateOf(playerName).pendingEvents
    fun inventoryOf(playerName: PlayerName) = stateOf(playerName).inventory
    fun positionOf(playerName: PlayerName) = stateOf(playerName).position
    fun gamesOf(playerName: PlayerName) = gameHistory[playerName]!!

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

    fun positionAmongPlayers(playerName: PlayerName): Int {
        val positions = stateSnapshot.playersState.values.map { it.position }.distinct().sortedDescending()
        return positions.indexOf(positionOf(playerName))
    }

    fun updateCurrentGame(playerName: PlayerName): GlobalState {
        return updatePlayer(playerName) { state ->
            state.copy(
                currentGame = gamesOf(playerName).lastOrNull { it.status != Game.Status.Next }
            )
        }
    }

    fun updateGameHistory(
        playerName: PlayerName,
        block: (List<GameHistoryEntry>) -> List<GameHistoryEntry>
    ): GlobalState {
        return this.copy(gameHistory = gameHistory.mapValues { (player, history) ->
            if (player == playerName) {
                block(history)
            } else {
                history
            }
        })
    }

    fun updatePlayer(playerName: PlayerName, block: (PlayerState) -> PlayerState): GlobalState {
        return this.copy(stateSnapshot = stateSnapshot.copy(
            playersState = stateSnapshot.playersState.mapValues { (player, playerState) ->
                if (player == playerName) {
                    block(playerState)
                } else {
                    playerState
                }
            }
        ))
    }

    fun updatePlayers(block: (PlayerName, PlayerState) -> PlayerState): GlobalState {
        return this.copy(stateSnapshot = stateSnapshot.copy(
            playersState = stateSnapshot.playersState.mapValues { (playerName, state) ->
                block(playerName, state)
            }
        ))
    }

    fun defaultPlayersHistory() = players.associate { it.name to PlayerTurnsHistory.default() }

    fun getMostPopulatedStintIndex(): Int {
        val indices = stateSnapshot.playersState
            .map { (_, state) -> state.stintIndex }
        val indexMap = mutableMapOf<Int, Int>()
        for (index in indices) {
            indexMap[index] = indexMap[index]?.plus(1) ?: 1
        }
        var maxAt = -1
        var maxCount = 0
        for ((index, count) in indexMap) {
            if (count > maxCount) {
                maxAt = index
                maxCount = count
            }
        }
        return if (maxCount > 1) {
            maxAt
        } else {
            indices.average().roundToInt()
        }
    }
}
