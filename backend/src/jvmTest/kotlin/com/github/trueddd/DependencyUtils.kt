package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.EventHistoryHolder
import com.github.trueddd.core.history.MockedLocalEventHistoryHolder
import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getItemFactoriesSet

internal fun provideEventGate(): EventGate {
    val stateHolder = StateHolderImpl()
    val gamesProvider = MockedGamesProvider()
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
    return MockedLocalEventHistoryHolder(actionHandlerRegistry)
}
