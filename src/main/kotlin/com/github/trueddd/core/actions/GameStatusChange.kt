package com.github.trueddd.core.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.Gamer
import com.github.trueddd.data.items.Viewer
import com.github.trueddd.data.items.WheelItem
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class GameStatusChange(
    val participant: Participant,
    val gameNewStatus: Game.Status,
) : Action(Key.GameStatusChange) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator : Action.Generator<GameStatusChange> {

        override val actionKey = Key.GameStatusChange

        override fun generate(participant: Participant, arguments: List<String>): GameStatusChange {
            val newStatus = arguments.firstOrNull()?.toIntOrNull()
                ?.let { Game.Status.entries.getOrNull(it) }
                ?: throw ActionCreationException("Couldn't parse new status from arguments: `$arguments`")
            return GameStatusChange(participant, newStatus)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Key.GameStatusChange)
    class Handler : Action.Handler<GameStatusChange> {

        override suspend fun handle(action: GameStatusChange, currentState: GlobalState): GlobalState {
            return currentState.updatePlayer(action.participant) { state ->
                val currentGame = state.gameHistory.lastOrNull()
                    ?: throw StateModificationException(action, "No game entries")
                val newGameHistory = state.gameHistory.dropLast(1) + currentGame.copy(status = action.gameNewStatus)
                val newEffects = if (action.gameNewStatus.allowsNextStep) {
                    state.effects.mapNotNull { effect ->
                        when (effect) {
                            is Gamer -> if (!effect.isActive) effect.setActive(true) else effect.charge() as? WheelItem.Effect
                            is Viewer -> if (!effect.isActive) effect.setActive(true) else effect.charge() as? WheelItem.Effect
                            else -> effect
                        }
                    }
                } else {
                    state.effects
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
