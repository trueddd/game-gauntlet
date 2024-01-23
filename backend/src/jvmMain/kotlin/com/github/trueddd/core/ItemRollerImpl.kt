package com.github.trueddd.core

import com.github.trueddd.items.WheelItem
import com.trueddd.github.annotations.ItemFactory
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ItemRollerImpl(
    @Named(ItemFactory.TAG)
    override val allItemsFactories: Set<WheelItem.Factory>,
) : ItemRoller {

    override fun pick(): WheelItem {
        return allItemsFactories.random().create()
    }
}
