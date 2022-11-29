package com.github.trueddd.di

import org.koin.dsl.module

val multibindingModule = module {

    single { getActionGenerators(get(), get()) }
}
