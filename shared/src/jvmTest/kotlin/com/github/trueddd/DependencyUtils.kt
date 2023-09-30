package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.core.history.MockedLocalEventHistoryHolder
import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getItemFactoriesSet

internal fun provideEventGate(): EventGate {
    val stateHolder = StateHolder()
    val gamesProvider = GamesProvider()
    val itemRoller = ItemRoller(getItemFactoriesSet())
    val actionHandlerRegistry = provideActionHandlerRegistry(gamesProvider, itemRoller)
    val inputParser = provideInputParser(stateHolder, gamesProvider, itemRoller)
    val eventManager = EventManager(actionHandlerRegistry, stateHolder)
    val historyHolder = provideHistoryHolder(actionHandlerRegistry)
    return EventGate(stateHolder, inputParser, eventManager, historyHolder)
}

internal fun provideInputParser(
    stateHolder: StateHolder = StateHolder(),
    gamesProvider: GamesProvider = GamesProvider(),
    itemRoller: ItemRoller = ItemRoller(getItemFactoriesSet())
) = InputParser(
    getActionGeneratorsSet(gamesProvider, itemRoller),
    stateHolder
)

private fun provideActionHandlerRegistry(gamesProvider: GamesProvider, itemRoller: ItemRoller) = ActionHandlerRegistry(
    handlers = getActionHandlersMap(gamesProvider, itemRoller)
)

private fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): EventHistoryHolder {
    return MockedLocalEventHistoryHolder(actionHandlerRegistry)
}
