package com.github.trueddd.plugins

import com.github.trueddd.data.ActionsTable
import com.github.trueddd.data.PlayersTable
import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getItemFactoriesSet
import com.github.trueddd.utils.Environment
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import com.trueddd.github.annotations.ItemFactory
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
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
    single(createdAtStart = true) {
        Database.connect(
            url = Environment.DatabaseUrl,
            driver = "org.postgresql.Driver",
            user = Environment.DatabaseUser,
            password = Environment.DatabasePassword,
        ).apply {
            transaction {
                SchemaUtils.createMissingTablesAndColumns(ActionsTable, PlayersTable)
            }
        }
    }
}

fun Application.configureDI() {
    install(Koin) {
        modules(defaultModule, commonModule)
    }
}
