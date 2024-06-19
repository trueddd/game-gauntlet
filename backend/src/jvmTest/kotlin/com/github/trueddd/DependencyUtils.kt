package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.data.repository.FileGameStateRepository
import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getItemFactoriesSet
import java.io.File

internal fun provideEventGate(): EventGate {
    val stateHolder = StateHolderImpl()
    val gamesProvider = GamesProviderImpl()
    val itemRoller = ItemRollerImpl(getItemFactoriesSet())
    val actionHandlerRegistry = provideActionHandlerRegistry(gamesProvider, itemRoller)
    val inputParser = provideInputParser(stateHolder, gamesProvider, itemRoller)
    val eventManager = EventManagerImpl(actionHandlerRegistry, stateHolder)
    val historyHolder = provideHistoryHolder(actionHandlerRegistry)
    return EventGateImpl(stateHolder, inputParser, eventManager, historyHolder)
}

internal fun provideInputParser(
    stateHolder: StateHolderImpl = StateHolderImpl(),
    gamesProvider: GamesProvider = GamesProviderImpl(),
    itemRoller: ItemRoller = ItemRollerImpl(getItemFactoriesSet())
): InputParser = InputParserImpl(
    getActionGeneratorsSet(gamesProvider, itemRoller),
    stateHolder
)

private fun provideActionHandlerRegistry(
    gamesProvider: GamesProvider,
    itemRoller: ItemRoller
): ActionHandlerRegistry = ActionHandlerRegistryImpl(
    handlers = getActionHandlersMap(gamesProvider, itemRoller)
)

private fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): EventHistoryHolder {
    val file = File(".\\src\\jvmTest\\resources\\history")
    return LocalEventHistoryHolder(actionHandlerRegistry, FileGameStateRepository(file))
}
