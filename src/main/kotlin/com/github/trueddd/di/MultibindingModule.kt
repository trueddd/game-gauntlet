package com.github.trueddd.di

import org.koin.dsl.module

val multibindingModule = module {

    single { getActionGeneratorSet(get(), get()) }

    single { getActionConsumerMap() }

    single { getItemFactorySet() }
}
