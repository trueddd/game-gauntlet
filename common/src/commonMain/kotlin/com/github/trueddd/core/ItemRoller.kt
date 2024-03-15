package com.github.trueddd.core

import com.github.trueddd.items.WheelItem

interface ItemRoller {

    val allItemsFactories: Set<WheelItem.Factory>

    val allItemsNames: List<String>

    fun pick(): WheelItem
}
