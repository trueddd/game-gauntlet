package com.github.trueddd.data

import com.github.trueddd.data.items.DiceRollModifier
import com.github.trueddd.data.items.WheelItem

data class PlayerState(
    val position: Int = 0,
    val inventory: List<WheelItem.InventoryItem> = emptyList(),
    val effects: List<WheelItem.Effect> = emptyList(),
) {

    val diceModifier: Int
        get() = inventory
            .filterIsInstance<DiceRollModifier>()
            .sumOf { it.modifier }
            .plus(effects
                .filterIsInstance<DiceRollModifier>()
                .sumOf { it.modifier }
            )
}
