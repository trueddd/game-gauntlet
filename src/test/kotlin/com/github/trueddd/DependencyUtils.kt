package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.core.history.MockedLocalEventHistoryHolder
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getItemFactorySet

internal fun provideEventGate(): EventGate {
    val stateHolder = StateHolder()
    val gamesProvider = GamesProvider()
    val actionHandlerRegistry = provideActionHandlerRegistry(gamesProvider)
    val inputParser = provideInputParser(gamesProvider)
    val eventManager = EventManager(actionHandlerRegistry, stateHolder)
    val historyHolder = provideHistoryHolder(actionHandlerRegistry)
    return EventGate(stateHolder, inputParser, eventManager, historyHolder)
}

internal fun provideInputParser(gamesProvider: GamesProvider = GamesProvider()) = InputParser(
    getActionGeneratorsSet(
        gamesProvider,
        ItemRoller(getItemFactorySet()),
    )
)

private fun provideActionHandlerRegistry(gamesProvider: GamesProvider) = ActionHandlerRegistry(
    handlers = getActionHandlersMap(gamesProvider)
)

private fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): EventHistoryHolder {
    return MockedLocalEventHistoryHolder(actionHandlerRegistry)
}
