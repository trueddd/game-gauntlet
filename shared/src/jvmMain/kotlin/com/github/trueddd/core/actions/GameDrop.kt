package com.github.trueddd.core.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.data.items.ClimbingRope
import com.github.trueddd.data.items.EasterCakeBang
import com.github.trueddd.data.items.SamuraiLunge
import com.github.trueddd.utils.StateModificationException
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.Serializable

@Serializable
data class GameDrop(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Key.GameDrop) {

    @ActionGenerator
    class Generator : Action.Generator<GameDrop> {

        override val actionKey = Key.GameDrop

        override fun generate(participant: Participant, arguments: List<String>): GameDrop {
            val dice = rollDice()
            return GameDrop(participant, dice)
        }
    }

    @ActionHandler(key = Key.GameDrop)
    class Handler : Action.Handler<GameDrop> {

        override suspend fun handle(action: GameDrop, currentState: GlobalState): GlobalState {
            val currentGame = currentState[action.rolledBy.name]?.currentGame
                ?: throw StateModificationException(action, "No game to drop")
            if (currentGame.status.isComplete) {
                throw StateModificationException(action, "Current game is already complete")
            }
            if (currentState.effectsOf(action.rolledBy).any { it is EasterCakeBang }) {
                throw StateModificationException(action, "Cannot drop the game due to player's debuff")
            }
            return currentState.updatePlayer(action.rolledBy) { playerState ->
                val moveValue = when {
                    playerState.effects.any { it is SamuraiLunge.Buff } -> action.diceValue
                    playerState.effects.any { it is ClimbingRope.Buff } -> -1
                    else -> -action.diceValue
                }
                val finalPosition = (playerState.position + moveValue).coerceIn(GlobalState.PLAYABLE_BOARD_RANGE)
                val newGameHistory = playerState.gameHistory.lastOrNull()
                    ?.copy(status = Game.Status.Dropped)
                    ?.let { playerState.gameHistory.dropLast(1) + it }
                    ?: playerState.gameHistory
                playerState.copy(
                    position = finalPosition,
                    effects = playerState.effects
                        .without<SamuraiLunge.Buff>()
                        .without<ClimbingRope.Buff>(),
                    gameHistory = newGameHistory,
                    boardMoveAvailable = false,
                )
            }
        }
    }
}
