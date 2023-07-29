package com.github.trueddd.core.actions

import com.github.trueddd.core.GamesProvider
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameHistoryEntry
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.ActionGeneratorCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class GameRoll(
    val participant: Participant,
    val gameId: Game.Id,
) : Action(Keys.GAME_ROLL) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator(private val gamesProvider: GamesProvider) : Action.Generator<GameRoll> {

        override val inputMatcher by lazy {
            Regex("${Commands.GAME_ROLL} ${Action.Generator.RegExpGroups.USER}", RegexOption.DOT_MATCHES_ALL)
        }

        override fun generate(matchResult: MatchResult): GameRoll {
            val participant = matchResult.groupValues.getOrNull(1)?.let { Participant(it) }
                ?: throw ActionGeneratorCreationException("Couldn't parse participant from input: `${matchResult.value}`")
            val game = gamesProvider.roll()
            return GameRoll(participant, game.id)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Keys.GAME_ROLL)
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
                state.copy(gameHistory = newGameHistory)
            }
        }
    }
}
