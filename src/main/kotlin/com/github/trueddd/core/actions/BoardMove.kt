package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.StateModificationException
import com.github.trueddd.utils.coerceDiceValue
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class BoardMove(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Keys.BoardMove) {

    @IntoSet(Action.Generator.SetTag)
    class Generator : Action.Generator<BoardMove> {

        override val inputMatcher by lazy {
            Regex("${Commands.BoardMove} ${Action.Generator.ParticipantGroup}", RegexOption.DOT_MATCHES_ALL)
        }

        override fun generate(matchResult: MatchResult): BoardMove {
            val actor = matchResult.groupValues.lastOrNull()!!
            val dice = rollDice()
            return BoardMove(Participant(actor), dice)
        }
    }

    @IntoMap(mapName = Action.Handler.MapTag, key = Keys.BoardMove)
    class Handler : Action.Handler<BoardMove> {

        override suspend fun handle(action: BoardMove, currentState: GlobalState): GlobalState {
            if (currentState.players[action.rolledBy]?.boardMoveAvailable == false) {
                throw StateModificationException(action, "Move is not available")
            }
            val newState = currentState.updatePlayer(action.rolledBy) { playerState ->
                val moveValue = coerceDiceValue(action.diceValue + playerState.diceModifier)
                val finalPosition = minOf(playerState.position + moveValue, currentState.boardLength)
                playerState.copy(
                    position = finalPosition,
                    stepsCount = playerState.stepsCount + 1,
                    boardMoveAvailable = false,
                )
            }
            val winner = newState.players.entries
                .firstOrNull { (_, state) -> state.position == currentState.boardLength }
                ?.key
            return newState.copy(
                winner = currentState.winner ?: winner,
            )
        }
    }
}
