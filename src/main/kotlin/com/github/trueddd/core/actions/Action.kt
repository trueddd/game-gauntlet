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

    object Keys {
        const val BOARD_MOVE = 1
        const val GAME_DROP = 2
        const val ITEM_RECEIVE = 3
        const val ITEM_USE = 4
        const val GAME_STATUS_CHANGE = 5
        const val GAME_ROLL = 6
    }

    object Commands {
        const val BOARD_MOVE = "move"
        const val GAME_DROP = "drop"
        const val ITEM_RECEIVE = "item"
        const val ITEM_USE = "use"
        const val GAME_STATUS_CHANGE = "game"
        const val GAME_ROLL = "roll-game"
    }

    /**
     * Action generator is a component that creates actions from entered command by any of participants.
     */
    interface Generator<out A : Action> {

        companion object {
            const val SET_TAG = "ActionGenerators"
        }

        object RegExpGroups {
            const val USER = "([a-z]+)"
            const val NUMBER = "(\\d+)"
            const val ITEM_UID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
        }

        val inputMatcher: Regex

        fun generate(matchResult: MatchResult): A
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
