package com.github.trueddd.data.items

import kotlinx.serialization.Serializable

@Serializable
class PowerThrow(
    override val modifier: Int = 1,
) : InventoryItem.Effect.Buff(), DiceRollModifier {

    override val id = Id.PowerThrow

    override val name = "Мощный бросок"

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }
}
