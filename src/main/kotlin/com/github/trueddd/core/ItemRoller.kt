package com.github.trueddd.core

import com.github.trueddd.data.items.WheelItem
import com.trueddd.github.annotations.ItemFactory
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ItemRoller(
    @Named(ItemFactory.TAG)
    private val allItemsFactories: Set<WheelItem.Factory>,
) {

    fun pick(): WheelItem {
        return allItemsFactories.random().create()
    }
}
