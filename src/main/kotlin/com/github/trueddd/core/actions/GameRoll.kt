package com.github.trueddd.core.actions

import com.github.trueddd.core.GamesProvider
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameHistoryEntry
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.YourStream
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.Serializable

@Serializable
data class GameRoll(
    val participant: Participant,
    val gameId: Game.Id,
) : Action(Key.GameRoll) {

    @ActionGenerator
    class Generator(private val gamesProvider: GamesProvider) : Action.Generator<GameRoll> {

        override val actionKey = Key.GameRoll

        override fun generate(participant: Participant, arguments: List<String>): GameRoll {
            val game = gamesProvider.roll()
            return GameRoll(participant, game.id)
        }
    }

    @ActionHandler(key = Key.GameRoll)
    class Handler(private val gamesProvider: GamesProvider) : Action.Handler<GameRoll> {

        override suspend fun handle(action: GameRoll, currentState: GlobalState): GlobalState {
            val currentGame = currentState[action.participant.name]?.gameHistory?.lastOrNull()
            if (currentGame != null && !currentGame.status.isComplete) {
                throw StateModificationException(action, "Current game is not finished ($currentGame)")
            }
            return currentState.updatePlayer(action.participant) { state ->
                val newGameHistory = gamesProvider.getById(action.gameId)?.let {
                    state.gameHistory + GameHistoryEntry(it, Game.Status.InProgress)
                } ?: throw StateModificationException(action, "Game with id (${action.gameId.value}) not found")
                val indexOfYourStreamBuff = state.effects.indexOfFirst { it is YourStream }
                val newEffects = state.effects.filterIndexed { index, _ -> index != indexOfYourStreamBuff }
                state.copy(
                    gameHistory = newGameHistory,
                    effects = newEffects
                )
            }
        }
    }
}
