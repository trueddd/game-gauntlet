package com.github.trueddd.actions

import com.github.trueddd.core.GamesProvider
import com.github.trueddd.data.*
import com.github.trueddd.items.FewLetters
import com.github.trueddd.items.IWouldBeatIt
import com.github.trueddd.items.YourStream
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a${Action.Key.GameRoll}")
data class GameRoll(
    @SerialName("rb")
    val playerName: PlayerName,
    @SerialName("gi")
    val gameId: Game.Id,
) : Action(Key.GameRoll) {

    @ActionGenerator
    class Generator(private val gamesProvider: GamesProvider) : Action.Generator<GameRoll> {

        override val actionKey = Key.GameRoll

        override fun generate(playerName: PlayerName, arguments: List<String>): GameRoll {
            val game = arguments.firstOrNull()?.toIntOrNull()
                ?.let { gamesProvider.getById(Game.Id(it)) }
                ?: gamesProvider.roll()
            return GameRoll(playerName, game.id)
        }
    }

    @ActionHandler(key = Key.GameRoll)
    class Handler(private val gamesProvider: GamesProvider) : Action.Handler<GameRoll> {

        override suspend fun handle(action: GameRoll, currentState: GlobalState): GlobalState {
            val currentGame = currentState.stateOf(action.playerName).currentGame
            if (currentState.positionOf(action.playerName) == 0) {
                throw StateModificationException(action, "Cannot roll games on Start position")
            }
            if (currentGame != null && !currentGame.status.isComplete) {
                throw StateModificationException(action, "Current game is not finished ($currentGame)")
            }
            val newGame = gamesProvider.getById(action.gameId)
                ?: throw StateModificationException(action, "Game with id (${action.gameId.value}) not found")
            if (currentState.effectsOf(action.playerName).any { it is IWouldBeatIt }
                && action.gameId !in currentState.getDroppedGames()
                && currentState.getDroppedGames().isNotEmpty()) {
                throw StateModificationException(action, "Player has to roll next game from the dropped ones")
            }
            if (currentState.effectsOf(action.playerName).any { it is FewLetters }
                && newGame.name.count { it.isLetterOrDigit() } > FewLetters.SYMBOLS_LIMIT) {
                throw StateModificationException(action, "Cannot have this game while FewLetters debuff is applied")
            }
            val newGameHistory = GameHistoryEntry(newGame, Game.Status.InProgress)
            return currentState.updatePlayer(action.playerName) { state ->
                val indexOfYourStreamBuff = state.effects.indexOfFirst { it is YourStream }
                val indexOfFewLettersDebuff = state.effects.indexOfFirst { it is FewLetters }
                val newEffects = state.effects.filterIndexed { index, _ ->
                    index != indexOfYourStreamBuff && index != indexOfFewLettersDebuff
                }
                state.copy(
                    currentGame = newGameHistory,
                    effects = newEffects
                )
            }.updateGameHistory(action.playerName) { it + newGameHistory }
        }
    }
}
