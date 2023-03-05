package com.github.trueddd.core

import com.github.trueddd.data.items.WheelItem
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ItemRoller(
    @Named(WheelItem.Factory.SET_NAME)
    private val allItemsFactories: Set<WheelItem.Factory>,
) {

    fun pick(): WheelItem {
        return allItemsFactories.random().create()
    }
}
