package com.github.trueddd.di

import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.items.WheelItem
import org.koin.core.qualifier.named
import org.koin.dsl.module

val multibindingModule = module {

    single(named(Action.Generator.SetTag)) { getActionGeneratorsSet(get(), get()) }

    single(named(Action.Handler.MapTag)) { getActionHandlersMap(get()) }

    single(named(WheelItem.Factory.SET_NAME)) { getItemFactorySet() }
}
