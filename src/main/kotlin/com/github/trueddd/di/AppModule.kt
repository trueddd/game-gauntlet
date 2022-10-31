package com.github.trueddd.di

import com.github.trueddd.core.ActionHandlerRegistry
import com.github.trueddd.core.EventManager
import com.github.trueddd.core.ItemRoller
import com.github.trueddd.core.InputParser
import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.core.history.LocalEventHistoryHolder
import org.koin.dsl.module

val appModule = module {

    single { ItemRoller() }

    single { ActionHandlerRegistry() }

    single<EventHistoryHolder> { LocalEventHistoryHolder(actionHandlerRegistry = get()) }

    single { EventManager(actionHandlerRegistry = get(), eventHistoryHolder = get()) }

    single { InputParser(globalStateFlow = get<EventManager>().globalStateFlow, itemRoller = get()) }
}
