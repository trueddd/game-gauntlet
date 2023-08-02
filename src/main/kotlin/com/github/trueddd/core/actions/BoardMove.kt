package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.*
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
) : Action(Key.BoardMove) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator : Action.Generator<BoardMove> {

        override val actionKey = Key.BoardMove

        override fun generate(participant: Participant, arguments: List<String>): BoardMove {
            val dice = rollDice()
            return BoardMove(participant, dice)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Key.BoardMove)
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
                            is PowerThrow -> effect.charge() as? WheelItem.Effect
                            is WeakThrow -> effect.charge() as? WheelItem.Effect
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
