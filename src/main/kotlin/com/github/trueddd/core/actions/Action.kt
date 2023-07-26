package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.DateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

/**
 * Action describes something happening to the global state or player state.
 * For example, receiving of the item by the participant of the game.
 * @param id is a unique ID of the action.
 * @param singleShot defines whether the action should be tracked in history and therefore reapplied on session restore.
 */
@Serializable
sealed class Action(
    open val id: Int,
    @Serializable(with = DateSerializer::class)
    val issuedAt: Date = Date(),
    @Transient
    val singleShot: Boolean = false,
) {

    object Keys {
        const val BoardMove = 1
        const val GameDrop = 2
        const val ItemReceive = 3
        const val ItemUse = 4
        const val GameStatusChange = 5
        const val GameRoll = 6
    }

    object Commands {
        const val BoardMove = "move"
        const val GameDrop = "drop"
        const val ItemReceive = "item"
        const val ItemUse = "use"
        const val GameStatusChange = "game"
        const val GameRoll = "roll-game"
    }

    /**
     * Action generator is a component that creates actions from entered command by any of participants.
     */
    interface Generator<out A : Action> {

        companion object {
            const val SetTag = "ActionGenerators"
            const val ParticipantGroup = "([a-z]+)"
            const val NumberGroup = "(\\d+)"
        }

        val inputMatcher: Regex

        fun generate(matchResult: MatchResult): A
    }

    /**
     * Action handler applies changes to the global state of the game according to the passed action.
     */
    interface Handler<in A : Action> {

        companion object {
            const val MapTag = "ActionHandlers"
        }

        suspend fun handle(action: A, currentState: GlobalState): GlobalState
    }
}
