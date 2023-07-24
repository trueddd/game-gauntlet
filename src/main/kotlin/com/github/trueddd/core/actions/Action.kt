package com.github.trueddd.core.actions

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
    }
}
