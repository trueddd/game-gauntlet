package com.github.trueddd.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.PlayerState
import com.github.trueddd.items.*
import com.github.trueddd.utils.*
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a${Action.Key.BoardMove}")
data class BoardMove(
    @SerialName("rb")
    val rolledBy: Participant,
    @SerialName("dv")
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
            if (!currentState.stateOf(action.rolledBy).boardMoveAvailable) {
                throw StateModificationException(action, "Move is not available")
            }
            val previousStintIndex = currentState.stateOf(action.rolledBy).stintIndex
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
                            currentState.stateSnapshot.boardTraps[it] is BananaSkin.Trap -> {
                                trapsToClear.add(it)
                                it - BananaSkin.STEPS_BACK
                            }
                            else -> it
                        }
                    }
                val newStintIndex = PlayerState.calculateStintIndex(finalPosition)
                val modifiersToDiscard = modifiers.filterIsInstance<WheelItem.Effect>().map { it.uid }
                val luckyThrowActivated = playerState.effects
                    .filterIsInstance<LuckyThrow.Buff>()
                    .any { it.genre == currentState.gameGenreDistribution.genreAtPosition(finalPosition) }
                playerState.copy(
                    position = finalPosition,
                    stepsCount = playerState.stepsCount + 1,
                    boardMoveAvailable = luckyThrowActivated,
                    effects = playerState.effects.mapNotNull { effect ->
                        when {
                            effect is LuckyThrow.Buff -> null
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
                    currentGame = currentState.gamesOf(action.rolledBy).lastOrNull { it.status == Game.Status.Next },
                )
            }
            val winner = newState.stateSnapshot.playersState.entries
                .firstOrNull { (_, state) -> state.position == currentState.boardLength }
                ?.key
            return newState.copy(
                stateSnapshot = newState.stateSnapshot.copy(
                    winner = newState.stateSnapshot.winner ?: winner,
                    boardTraps = newState.stateSnapshot.boardTraps.filterKeys { it !in trapsToClear },
                ),
            )
        }
    }
}
