package com.github.trueddd.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.di.ActionIssueDateComponentHolder
import com.github.trueddd.utils.StateModificationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Action describes something happening to the global state or player state.
 * For example, receiving of the item by the participant of the game.
 * @param id is a unique ID of the action.
 */
@Serializable
sealed class Action(
    @SerialName("id")
    open val id: Int,
    @SerialName("ia")
    val issuedAt: Long = ActionIssueDateComponentHolder.get().getIssueDate(),
) {

    @Suppress("ConstPropertyName")
    object Key {
        const val BoardMove = 1
        const val GameDrop = 2
        const val ItemReceive = 3
        const val ItemUse = 4
        const val GameStatusChange = 5
        const val GameRoll = 6
        const val GameSet = 7
        const val GlobalEvent = 8
    }

    /**
     * Action generator is a component that creates actions from entered command by any participant.
     */
    interface Generator<out A : Action> {
        val actionKey: Int
        fun generate(playerName: PlayerName, arguments: List<String>): A
    }

    /**
     * Action handler applies changes to the global state of the game according to the passed action.
     */
    interface Handler<in A : Action> {

        @Throws(StateModificationException::class)
        suspend fun handle(action: A, currentState: GlobalState): GlobalState
    }
}
