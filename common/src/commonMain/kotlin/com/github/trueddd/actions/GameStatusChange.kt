package com.github.trueddd.actions

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.items.*
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: split this action to more specific ones?
@Serializable
@SerialName("a${Action.Key.GameStatusChange}")
data class GameStatusChange(
    @SerialName("p")
    val playerName: PlayerName,
    @SerialName("s")
    val gameNewStatus: Game.Status,
) : Action(Key.GameStatusChange) {

    @ActionGenerator
    class Generator : Action.Generator<GameStatusChange> {

        override val actionKey = Key.GameStatusChange

        override fun generate(playerName: PlayerName, arguments: List<String>): GameStatusChange {
            val newStatus = arguments.firstOrNull()?.toIntOrNull()
                ?.let { Game.Status.entries.getOrNull(it) }
                ?: throw ActionCreationException("Couldn't parse new status from arguments: `$arguments`")
            return GameStatusChange(playerName, newStatus)
        }
    }

    @ActionHandler(key = Key.GameStatusChange)
    class Handler : Action.Handler<GameStatusChange> {

        override suspend fun handle(action: GameStatusChange, currentState: GlobalState): GlobalState {
            val currentGame = currentState.stateOf(action.playerName).currentActiveGame
                ?: throw StateModificationException(action, "No game entries")
            val nextGame = currentState.gamesOf(action.playerName).firstOrNull { it.status == Game.Status.Next }
            val isStatusFinished = action.gameNewStatus == Game.Status.Finished
            val newPendingEvents = currentState.pendingEventsOf(action.playerName).mapNotNull { pendingEvent ->
                when (pendingEvent) {
                    is FamilyFriendlyStreamer -> pendingEvent.takeUnless { isStatusFinished }
                    else -> pendingEvent
                }
            }
            val newEffects = currentState.effectsOf(action.playerName).mapNotNull { effect ->
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
                    is EasterCakeBang -> if (isStatusFinished) null else effect
                    is Radio -> if (isStatusFinished) effect.charge() else effect
                    is ClimbingRope.Buff -> null
                    is ThereIsGiftAtYourDoor.StayAfterGame -> null
                    else -> effect
                }
            }
            return currentState.updatePlayer(action.playerName) { state ->
                state.copy(
                    boardMoveAvailable = when {
                        state.effects.any { it is ThereIsGiftAtYourDoor.StayAfterGame } -> false
                        action.gameNewStatus.allowsNextStep -> true
                        else -> state.boardMoveAvailable
                    },
                    effects = newEffects,
                    pendingEvents = newPendingEvents,
                )
            }.updateGameHistory(action.playerName) { history ->
                history.map { entry ->
                    when (entry.game) {
                        currentGame.game -> entry.copy(status = action.gameNewStatus)
                        nextGame?.game -> entry.copy(status = Game.Status.InProgress)
                        else -> entry
                    }
                }
            }.updateCurrentGame(action.playerName)
        }
    }
}
