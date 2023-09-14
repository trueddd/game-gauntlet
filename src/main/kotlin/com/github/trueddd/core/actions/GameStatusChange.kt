package com.github.trueddd.core.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.ClimbingRope
import com.github.trueddd.data.items.Gamer
import com.github.trueddd.data.items.Viewer
import com.github.trueddd.data.items.charge
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.Serializable

// TODO: split this action to more specific ones or include game drop logics here?
@Serializable
data class GameStatusChange(
    val participant: Participant,
    val gameNewStatus: Game.Status,
) : Action(Key.GameStatusChange) {

    @ActionGenerator
    class Generator : Action.Generator<GameStatusChange> {

        override val actionKey = Key.GameStatusChange

        override fun generate(participant: Participant, arguments: List<String>): GameStatusChange {
            val newStatus = arguments.firstOrNull()?.toIntOrNull()
                ?.let { Game.Status.entries.getOrNull(it) }
                ?: throw ActionCreationException("Couldn't parse new status from arguments: `$arguments`")
            return GameStatusChange(participant, newStatus)
        }
    }

    @ActionHandler(key = Key.GameStatusChange)
    class Handler : Action.Handler<GameStatusChange> {

        override suspend fun handle(action: GameStatusChange, currentState: GlobalState): GlobalState {
            return currentState.updatePlayer(action.participant) { state ->
                val currentGame = state.currentActiveGame
                    ?: throw StateModificationException(action, "No game entries")
                val nextGame = state.gameHistory.firstOrNull { it.status == Game.Status.Next }
                val newGameHistory = state.gameHistory.map { entry ->
                    when (entry.game) {
                        currentGame.game -> entry.copy(status = action.gameNewStatus)
                        nextGame?.game -> entry.copy(status = Game.Status.InProgress)
                        else -> entry
                    }
                }
                val newEffects = state.effects.mapNotNull { effect ->
                    when (effect) {
                        is Gamer -> when {
                            !action.gameNewStatus.allowsNextStep -> effect
                            !effect.isActive -> effect.setActive(true)
                            else -> effect.charge()
                        }
                        is Viewer -> when {
                            !action.gameNewStatus.allowsNextStep -> effect
                            !effect.isActive -> effect.setActive(true)
                            else -> effect.charge()
                        }
                        is ClimbingRope.Buff -> null
                        else -> effect
                    }
                }
                state.copy(
                    gameHistory = newGameHistory,
                    boardMoveAvailable = if (action.gameNewStatus.allowsNextStep) true else state.boardMoveAvailable,
                    effects = newEffects,
                )
            }
        }
    }
}
