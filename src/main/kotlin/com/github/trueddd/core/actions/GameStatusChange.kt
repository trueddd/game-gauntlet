package com.github.trueddd.core.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.ActionGeneratorCreationException
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

        override fun generate(userName: String, arguments: List<String>): GameStatusChange {
            val newStatus = arguments.firstOrNull()?.toIntOrNull()
                ?.let { Game.Status.entries.getOrNull(it) }
                ?: throw ActionGeneratorCreationException("Couldn't parse new status from arguments: `$arguments`")
            return GameStatusChange(Participant(userName), newStatus)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Key.GameStatusChange)
    class Handler : Action.Handler<GameStatusChange> {

        override suspend fun handle(action: GameStatusChange, currentState: GlobalState): GlobalState {
            return currentState.updatePlayer(action.participant) { state ->
                val currentGame = state.gameHistory.lastOrNull()
                    ?: throw StateModificationException(action, "No game entries")
                val newGameHistory = state.gameHistory.dropLast(1) + currentGame.copy(status = action.gameNewStatus)
                state.copy(
                    gameHistory = newGameHistory,
                    boardMoveAvailable = if (action.gameNewStatus.allowsNextStep) true else state.boardMoveAvailable,
                )
            }
        }
    }
}
