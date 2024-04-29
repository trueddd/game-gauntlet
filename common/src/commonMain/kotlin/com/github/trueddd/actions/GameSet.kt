package com.github.trueddd.actions

import com.github.trueddd.core.GamesProvider
import com.github.trueddd.data.*
import com.github.trueddd.items.DontCare
import com.github.trueddd.items.DontUnderstand
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a${Action.Key.GameSet}")
data class GameSet(
    @SerialName("sb")
    val setBy: Participant,
    @SerialName("gi")
    val gameId: Game.Id,
) : Action(Key.GameSet) {

    @ActionGenerator
    class Generator(private val gamesProvider: GamesProvider) : Action.Generator<GameSet> {

        override val actionKey = Key.GameSet

        override fun generate(participant: Participant, arguments: List<String>): GameSet {
            val gameId = arguments.firstOrNull()?.toIntOrNull()
                ?.let { gamesProvider.getById(Game.Id(it)) }?.id
                ?: throw ActionCreationException("Game Id was specified with error or game is not present")
            return GameSet(participant, gameId)
        }
    }

    @ActionHandler(key = Key.GameSet)
    class Handler(private val gamesProvider: GamesProvider) : Action.Handler<GameSet> {

        override suspend fun handle(action: GameSet, currentState: GlobalState): GlobalState {
            val game = gamesProvider.getById(action.gameId)
                ?: throw StateModificationException(action, "Game with Id (${action.gameId}) was not found")
            return when {
                currentState.effectsOf(action.setBy).any { it is DontCare } -> {
                    val entry = GameHistoryEntry(
                        game = game,
                        status = if (currentState.stateOf(action.setBy).hasCurrentActiveGame) {
                            Game.Status.Next
                        } else {
                            Game.Status.InProgress
                        }
                    )
                    currentState.updatePlayer(action.setBy) { playerState ->
                        playerState.copy(
                            effects = playerState.effects.without<DontCare>(),
                            currentGame = if (playerState.hasCurrentActiveGame) playerState.currentGame else entry,
                        )
                    }.copy(gameHistory = currentState.gameHistory.mapValues { (player, history) ->
                        if (player == action.setBy.name) {
                            history + entry
                        } else {
                            history
                        }
                    })
                }
                currentState.effectsOf(action.setBy).any { it is DontUnderstand } -> {
                    val entry = GameHistoryEntry(
                        game = game,
                        status = if (currentState.stateOf(action.setBy).hasCurrentActiveGame) {
                            Game.Status.Next
                        } else {
                            Game.Status.InProgress
                        }
                    )
                    currentState.updatePlayer(action.setBy) { playerState ->
                        playerState.copy(
                            effects = playerState.effects.without<DontUnderstand>(),
                            currentGame = if (playerState.hasCurrentActiveGame) playerState.currentGame else entry,
                        )
                    }.copy(gameHistory = currentState.gameHistory.mapValues { (player, history) ->
                        if (player == action.setBy.name) {
                            history + entry
                        } else {
                            history
                        }
                    })
                }
                else -> throw StateModificationException(
                    action,
                    cause = "Player doesn't have required buff to set the game by themselves"
                )
            }
        }
    }
}
