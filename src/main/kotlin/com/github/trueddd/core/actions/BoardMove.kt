package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.DiceRollModifier
import com.github.trueddd.data.items.WheelItem
import com.github.trueddd.data.items.WithCharges
import com.github.trueddd.utils.StateModificationException
import com.github.trueddd.utils.moveRange
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.absoluteValue

@Serializable
data class BoardMove(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Keys.BOARD_MOVE) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator : Action.Generator<BoardMove> {

        override val inputMatcher by lazy {
            Regex("${Commands.BOARD_MOVE} ${Action.Generator.RegExpGroups.USER}", RegexOption.DOT_MATCHES_ALL)
        }

        override fun generate(matchResult: MatchResult): BoardMove {
            val actor = matchResult.groupValues.lastOrNull()!!
            val dice = rollDice()
            return BoardMove(Participant(actor), dice)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Keys.BOARD_MOVE)
    class Handler : Action.Handler<BoardMove> {

        override suspend fun handle(action: BoardMove, currentState: GlobalState): GlobalState {
            if (currentState.players[action.rolledBy]?.boardMoveAvailable == false) {
                throw StateModificationException(action, "Move is not available")
            }
            val newState = currentState.updatePlayer(action.rolledBy) { playerState ->
                val modifiers = playerState.effects
                    .filterIsInstance<DiceRollModifier>()
                    .sortedBy { it.modifier.absoluteValue }
                    .let { LinkedList(it) }
                while (modifiers.sumOf { it.modifier } + action.diceValue !in moveRange && modifiers.isNotEmpty()) {
                    modifiers.removeAt(0)
                }
                val moveValue = modifiers.sumOf { it.modifier } + action.diceValue
                val finalPosition = minOf(playerState.position + moveValue, currentState.boardLength)
                playerState.copy(
                    position = finalPosition,
                    stepsCount = playerState.stepsCount + 1,
                    boardMoveAvailable = false,
                    effects = playerState.effects.mapNotNull { effect ->
                        when (effect) {
                            !is DiceRollModifier -> effect
                            !in modifiers -> effect
                            is WithCharges -> when (effect.chargesLeft) {
                                1 -> null
                                else -> effect.useCharge() as WheelItem.Effect
                            }
                            else -> null
                        }
                    },
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
