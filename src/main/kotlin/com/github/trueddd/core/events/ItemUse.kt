package com.github.trueddd.core.events

import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

@Serializable
data class ItemUse(
    val usedBy: Participant,
    val itemUid: Long,
) : Action(Keys.ItemUse)
