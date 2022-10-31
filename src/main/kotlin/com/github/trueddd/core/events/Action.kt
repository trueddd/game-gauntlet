package com.github.trueddd.core.events

import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.InventoryItem
import com.github.trueddd.utils.DateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

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
    }

    @Serializable
    data class BoardMove(
        val rolledBy: Participant,
        val diceValue: Int,
        val modifiers: Int,
    ) : Action(Keys.BoardMove)

    @Serializable
    data class GameDrop(
        val rolledBy: Participant,
        val diceValue: Int,
    ) : Action(Keys.GameDrop)

    @Serializable
    data class ItemReceive(
        val receivedBy: Participant,
        val item: InventoryItem,
    ) : Action(Keys.ItemReceive)
}
