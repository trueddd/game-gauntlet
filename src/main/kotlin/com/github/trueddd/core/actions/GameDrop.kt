package com.github.trueddd.core.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.DropReverse
import com.github.trueddd.utils.StateModificationException
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class GameDrop(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Keys.GAME_DROP) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator : Action.Generator<GameDrop> {

        override val inputMatcher by lazy {
            Regex("${Commands.GAME_DROP} ${Action.Generator.RegExpGroups.USER}", RegexOption.DOT_MATCHES_ALL)
        }

        override fun generate(matchResult: MatchResult): GameDrop {
            val actor = matchResult.groupValues.lastOrNull()!!
            val dice = rollDice()
            return GameDrop(Participant(actor), dice)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Keys.GAME_DROP)
    class Handler : Action.Handler<GameDrop> {

        override suspend fun handle(action: GameDrop, currentState: GlobalState): GlobalState {
            val currentGame = currentState[action.rolledBy.name]?.currentGameEntry
                ?: throw StateModificationException(action, "No game to drop")
            if (currentGame.status.isComplete) {
                throw StateModificationException(action, "Current game is already complete")
            }
            return currentState.updatePlayer(action.rolledBy) { playerState ->
                val moveValue = if (playerState.effects.any { it is DropReverse }) {
                    action.diceValue
                } else {
                    -action.diceValue
                }
                val finalPosition = max(playerState.position + moveValue, 0)
                val newGameHistory = playerState.gameHistory.lastOrNull()
                    ?.copy(status = Game.Status.Dropped)
                    ?.let { playerState.gameHistory.dropLast(1) + it }
                    ?: playerState.gameHistory
                playerState.copy(
                    position = finalPosition,
                    effects = playerState.effects.filter { it !is DropReverse },
                    gameHistory = newGameHistory,
                    boardMoveAvailable = false,
                )
            }
        }
    }
}
