package com.github.trueddd.actions

import com.github.trueddd.core.GamesProvider
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameHistoryEntry
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.items.FewLetters
import com.github.trueddd.items.IWouldBeatIt
import com.github.trueddd.items.YourStream
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
            if (currentState.positionOf(action.participant) == 0) {
                throw StateModificationException(action, "Cannot roll games on Start position")
            }
            if (currentGame != null && !currentGame.status.isComplete) {
                throw StateModificationException(action, "Current game is not finished ($currentGame)")
            }
            val newGame = gamesProvider.getById(action.gameId)
                ?: throw StateModificationException(action, "Game with id (${action.gameId.value}) not found")
            if (currentState.effectsOf(action.participant).any { it is IWouldBeatIt }
                && action.gameId !in currentState.getDroppedGames()
                && currentState.getDroppedGames().isNotEmpty()) {
                throw StateModificationException(action, "Player has to roll next game from the dropped ones")
            }
            if (currentState.effectsOf(action.participant).any { it is FewLetters }
                && newGame.name.count { it.isLetterOrDigit() } > FewLetters.SYMBOLS_LIMIT) {
                throw StateModificationException(action, "Cannot have this game while FewLetters debuff is applied")
            }
            return currentState.updatePlayer(action.participant) { state ->
                val newGameHistory = state.gameHistory + GameHistoryEntry(newGame, Game.Status.InProgress)
                val indexOfYourStreamBuff = state.effects.indexOfFirst { it is YourStream }
                val indexOfFewLettersDebuff = state.effects.indexOfFirst { it is FewLetters }
                val newEffects = state.effects.filterIndexed { index, _ ->
                    index != indexOfYourStreamBuff && index != indexOfFewLettersDebuff
                }
                state.copy(
                    gameHistory = newGameHistory,
                    effects = newEffects
                )
            }
        }
    }
}
