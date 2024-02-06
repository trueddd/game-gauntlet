package com.github.trueddd.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

object KoinIntegration {

    internal var koin: KoinApplication? = null

    fun start() {
        if (koin != null) {
            return
        }
        koin = startKoin {
            modules(module)
        }
    }
}

internal inline fun <reified T> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    return KoinIntegration.koin!!.koin.get(qualifier, parameters)
}
