package com.github.trueddd.core.events

import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.InventoryItem
import kotlinx.serialization.Serializable

@Serializable
data class ItemReceive(
    val receivedBy: Participant,
    val item: InventoryItem,
) : Action(Keys.ItemReceive)
