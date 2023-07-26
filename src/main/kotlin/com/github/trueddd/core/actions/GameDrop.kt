package com.github.trueddd.core.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.DropReverse
import com.github.trueddd.utils.StateModificationException
import com.github.trueddd.utils.coerceDiceValue
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class GameDrop(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Keys.GameDrop) {

    @IntoSet(Action.Generator.SetTag)
    class Generator : Action.Generator<GameDrop> {

        override val inputMatcher by lazy {
            Regex("${Commands.GameDrop} ${Action.Generator.ParticipantGroup}", RegexOption.DOT_MATCHES_ALL)
        }

        override fun generate(matchResult: MatchResult): GameDrop {
            val actor = matchResult.groupValues.lastOrNull()!!
            val dice = rollDice()
            return GameDrop(Participant(actor), dice)
        }
    }

    @IntoMap(mapName = Action.Handler.MapTag, key = Keys.GameDrop)
    class Handler : Action.Handler<GameDrop> {

        override suspend fun handle(action: GameDrop, currentState: GlobalState): GlobalState {
            val currentGame = currentState[action.rolledBy.name]?.currentGameEntry
                ?: throw StateModificationException(action, "No game to drop")
            if (currentGame.status.isComplete) {
                throw StateModificationException(action, "Current game is already complete")
            }
            return currentState.updatePlayer(action.rolledBy) { playerState ->
                val moveValue = if (playerState.effects.any { it is DropReverse }) {
                    coerceDiceValue(action.diceValue + playerState.diceModifier)
                } else {
                    -coerceDiceValue(action.diceValue)
                }
                val finalPosition = (playerState.position + moveValue).coerceAtLeast(0)
                val newGameHistory = playerState.gameHistory.lastOrNull()
                    ?.copy(status = Game.Status.Dropped)
                    ?.let { playerState.gameHistory.dropLast(1) + it }
                    ?: playerState.gameHistory
                playerState.copy(
                    position = finalPosition,
                    effects = playerState.effects.filter { it !is DropReverse },
                    gameHistory = newGameHistory
                )
            }
        }
    }
}
