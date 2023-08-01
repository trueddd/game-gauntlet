package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import kotlinx.serialization.Serializable

/**
 * Action describes something happening to the global state or player state.
 * For example, receiving of the item by the participant of the game.
 * @param id is a unique ID of the action.
 */
@Serializable
sealed class Action(
    open val id: Int,
    val issuedAt: Long = System.currentTimeMillis(),
) {

    @Suppress("ConstPropertyName")
    object Key {
        const val BoardMove = 1
        const val GameDrop = 2
        const val ItemReceive = 3
        const val ItemUse = 4
        const val GameStatusChange = 5
        const val GameRoll = 6
    }

    /**
     * Action generator is a component that creates actions from entered command by any of participants.
     */
    interface Generator<out A : Action> {

        companion object {
            const val SET_TAG = "ActionGenerators"
        }

        val actionKey: Int

        fun generate(userName: String, arguments: List<String>): A
    }

    /**
     * Action handler applies changes to the global state of the game according to the passed action.
     */
    interface Handler<in A : Action> {

        companion object {
            const val MAP_TAG = "ActionHandlers"
        }

        suspend fun handle(action: A, currentState: GlobalState): GlobalState
    }
}
