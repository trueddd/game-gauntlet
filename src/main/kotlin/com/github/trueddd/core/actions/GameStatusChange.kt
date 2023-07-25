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
) : Action(Keys.GameStatusChange) {

    @IntoSet(Action.Generator.SetTag)
    class Generator : Action.Generator<GameStatusChange> {

        override val inputMatcher by lazy {
            Regex(
                pattern = "game ${Action.Generator.ParticipantGroup} ${Action.Generator.NumberGroup}",
                option = RegexOption.DOT_MATCHES_ALL
            )
        }

        override fun generate(matchResult: MatchResult): GameStatusChange {
            val participant = matchResult.groupValues.getOrNull(1)?.let { Participant(it) }
                ?: throw ActionGeneratorCreationException("Couldn't parse participant from input: `${matchResult.value}`")
            val newStatus = matchResult.groupValues.getOrNull(2)?.toIntOrNull()
                ?.let { Game.Status.values().getOrNull(it) }
                ?: throw ActionGeneratorCreationException("Couldn't parse new status from input: `${matchResult.value}`")
            return GameStatusChange(participant, newStatus)
        }
    }

    @IntoMap(mapName = Action.Handler.MapTag, key = Keys.GameStatusChange)
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
