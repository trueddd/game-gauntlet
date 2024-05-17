package com.github.trueddd.plugins

import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getItemFactoriesSet
import com.github.trueddd.utils.Environment
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import com.trueddd.github.annotations.ItemFactory
import io.ktor.server.application.*
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
        val connectionString = buildString {
            append("mongodb+srv://default:")
            append(Environment.MongoPassword)
            append("@agg.yte3zhl.mongodb.net/")
            append("?retryWrites=true&w=majority&appName=agg")
        }
        MongoClient.create(connectionString).getDatabase("agg")
    }
}

fun Application.configureDI() {
    install(Koin) {
        modules(defaultModule, commonModule)
    }
}
