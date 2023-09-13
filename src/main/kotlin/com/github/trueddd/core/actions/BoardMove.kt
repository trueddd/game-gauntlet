package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.PlayerState
import com.github.trueddd.data.items.*
import com.github.trueddd.utils.*
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.Serializable

@Serializable
data class BoardMove(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Key.BoardMove) {

    init {
        require(diceValue in d6Range) { IllegalArgumentException("diceValue cannot be out of d6Range($d6Range)") }
    }

    @ActionGenerator
    class Generator : Action.Generator<BoardMove> {

        override val actionKey = Key.BoardMove

        override fun generate(participant: Participant, arguments: List<String>): BoardMove {
            val dice = rollDice()
            return BoardMove(participant, dice)
        }
    }

    @ActionHandler(key = Key.BoardMove)
    class Handler : Action.Handler<BoardMove> {

        override suspend fun handle(action: BoardMove, currentState: GlobalState): GlobalState {
            val trapsToClear = mutableListOf<Int>()
            if (currentState.players[action.rolledBy]?.boardMoveAvailable == false) {
                throw StateModificationException(action, "Move is not available")
            }
            val previousStintIndex = currentState[action.rolledBy.name]!!.stintIndex
            val newState = currentState.updatePlayer(action.rolledBy) { playerState ->
                val modifiers = playerState.effects
                    .filterIsInstance<DiceRollModifier>()
                    .powerSet()
                    .filter { modifiers -> modifiers.sumOf { it.modifier } + action.diceValue in moveRange }
                    .maxBy { it.size }
                val moveValue = (modifiers.sumOf { it.modifier } + action.diceValue)
                    .let { value -> if (playerState.effects.any { it is ChargedDice }) -value else value }
                val finalPosition = (playerState.position + moveValue).coerceIn(GlobalState.PLAYABLE_BOARD_RANGE)
                    .let {
                        when {
                            currentState.boardTraps[it] is BananaSkinTrap -> {
                                trapsToClear.add(it)
                                it - 2
                            }
                            else -> it
                        }
                    }
                val newStintIndex = PlayerState.calculateStintIndex(finalPosition)
                val modifiersToDiscard = modifiers.filterIsInstance<WheelItem.Effect>().map { it.uid }
                playerState.copy(
                    position = finalPosition,
                    stepsCount = playerState.stepsCount + 1,
                    boardMoveAvailable = false,
                    effects = playerState.effects.mapNotNull { effect ->
                        when {
                            effect is ChargedDice -> null
                            effect is NoClownery -> if (previousStintIndex + 1 == newStintIndex) null else effect
                            effect !is DiceRollModifier -> effect
                            modifiersToDiscard.none { it == effect.uid } -> effect
                            effect is BabySupport -> effect.charge()
                            effect is PowerThrow -> effect.charge()
                            effect is WeakThrow -> effect.charge()
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
                boardTraps = currentState.boardTraps.filterKeys { it !in trapsToClear },
            )
        }
    }
}
