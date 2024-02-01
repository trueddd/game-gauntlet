package com.github.trueddd.plugins

import com.github.trueddd.di.CommonModule
import io.ktor.server.application.*
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(defaultModule, CommonModule().module)
    }
}
