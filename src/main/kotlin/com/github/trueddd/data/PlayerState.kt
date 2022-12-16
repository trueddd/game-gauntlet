package com.github.trueddd.data

import com.github.trueddd.data.items.DiceRollModifier
import com.github.trueddd.data.items.InventoryItem

data class PlayerState(
    val position: Int = 0,
    val inventory: List<InventoryItem> = emptyList(),
) {

    val diceModifier: Int
        get() = inventory
            .filterIsInstance<DiceRollModifier>()
            .sumOf { it.modifier }
}
