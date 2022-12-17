package com.github.trueddd.di

import com.github.trueddd.core.generator.ActionGenerator
import com.github.trueddd.core.handler.ActionConsumer
import com.github.trueddd.data.items.InventoryItem
import org.koin.core.qualifier.named
import org.koin.dsl.module

val multibindingModule = module {

    single(named(ActionGenerator.TAG)) { getActionGeneratorSet(get()) }

    single(named(ActionConsumer.TAG)) { getActionConsumerMap() }

    single(named(InventoryItem.Factory.SET_NAME)) { getItemFactorySet() }
}
