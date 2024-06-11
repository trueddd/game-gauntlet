package com.github.trueddd.plugins

import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getItemFactoriesSet
import com.github.trueddd.utils.Environment
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import com.trueddd.github.annotations.ItemFactory
import io.ktor.server.application.*
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ktor.plugin.Koin

private val commonModule = module {
    single(named(ActionGenerator.TAG)) {
        getActionGeneratorsSet(gamesProvider = get(), itemRoller = get())
    }
    single(named(ActionHandler.TAG)) {
        getActionHandlersMap(gamesProvider = get(), itemRoller = get())
    }
    single(named(ItemFactory.TAG)) {
        getItemFactoriesSet()
    }
    single {
        MongoClient.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(Environment.DatabaseUrl))
                .applyToLoggerSettings {
                    logger.level = Level.ERROR
                }
                .build()
        ).getDatabase("agg")
    }
}

fun Application.configureDI() {
    install(Koin) {
        modules(defaultModule, commonModule)
    }
}
