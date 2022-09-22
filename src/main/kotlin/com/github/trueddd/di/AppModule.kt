package com.github.trueddd.di

import com.github.trueddd.core.EventManager
import com.github.trueddd.core.generator.InputParser
import org.koin.dsl.module

val appModule = module {

    single { EventManager() }

    single { InputParser(globalStateFlow = get<EventManager>().globalStateFlow) }
}
