package com.github.trueddd.plugins

import com.github.trueddd.di.multibindingModule
import io.ktor.server.application.*
import org.koin.ksp.generated.defaultModule
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(defaultModule, multibindingModule)
    }
}
