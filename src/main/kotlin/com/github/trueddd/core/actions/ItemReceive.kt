package com.github.trueddd.core.actions

import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.WheelItem
import kotlinx.serialization.Serializable

@Serializable
data class ItemReceive(
    val receivedBy: Participant,
    val item: WheelItem,
) : Action(Keys.ItemReceive)
