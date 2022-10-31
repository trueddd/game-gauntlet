package com.github.trueddd.core

import com.github.trueddd.data.items.InventoryItem
import com.github.trueddd.data.items.PowerThrow
import com.github.trueddd.data.items.WeakThrow
import com.github.trueddd.data.items.YouDoNotNeedThis

class ItemRoller {

    // TODO: provide list of available items using annotations
    private val allItems = listOf(
        { PowerThrow() },
        { WeakThrow() },
        { YouDoNotNeedThis() },
    )

    fun pick(): InventoryItem {
        return allItems.random().invoke()
    }
}
