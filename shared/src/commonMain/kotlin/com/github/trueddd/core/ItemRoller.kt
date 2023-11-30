package com.github.trueddd.core

import com.github.trueddd.items.WheelItem

interface ItemRoller {

    val allItemsFactories: Set<WheelItem.Factory>

    fun pick(): WheelItem
}
