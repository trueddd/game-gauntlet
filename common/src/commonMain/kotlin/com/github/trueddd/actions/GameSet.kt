package com.github.trueddd.actions

import com.github.trueddd.core.GamesProvider
import com.github.trueddd.data.*
import com.github.trueddd.items.DontCare
import com.github.trueddd.items.DontUnderstand
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.Serializable

@Serializable
data class GameSet(
    val setBy: Participant,
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
                    currentState.updatePlayer(action.setBy) { playerState ->
                        val entry = GameHistoryEntry(
                            game = game,
                            status = if (playerState.hasCurrentActive) Game.Status.Next else Game.Status.InProgress
                        )
                        playerState.copy(
                            effects = playerState.effects.without<DontCare>(),
                            gameHistory = playerState.gameHistory + entry,
                        )
                    }
                }
                currentState.effectsOf(action.setBy).any { it is DontUnderstand } -> {
                    currentState.updatePlayer(action.setBy) { playerState ->
                        val entry = GameHistoryEntry(
                            game = game,
                            status = if (playerState.hasCurrentActive) Game.Status.Next else Game.Status.InProgress
                        )
                        playerState.copy(
                            effects = playerState.effects.without<DontUnderstand>(),
                            gameHistory = playerState.gameHistory + entry,
                        )
                    }
                }
                else -> throw StateModificationException(
                    action,
                    cause = "Player doesn't have required buff to set the game by themselves"
                )
            }
        }
    }
}
