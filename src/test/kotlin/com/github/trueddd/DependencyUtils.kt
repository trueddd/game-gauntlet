@file:Suppress("UNCHECKED_CAST")

package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.actions.Action
import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.core.history.MockedLocalEventHistoryHolder
import com.github.trueddd.data.items.WheelItem
import com.github.trueddd.di.getActionHandlersMap
import com.github.trueddd.di.getActionGeneratorsSet
import com.github.trueddd.di.getItemFactoriesSet

internal fun provideEventGate(): EventGate {
    val stateHolder = StateHolder()
    val gamesProvider = GamesProvider()
    val actionHandlerRegistry = provideActionHandlerRegistry(gamesProvider)
    val inputParser = provideInputParser(stateHolder, gamesProvider)
    val eventManager = EventManager(actionHandlerRegistry, stateHolder)
    val historyHolder = provideHistoryHolder(actionHandlerRegistry)
    return EventGate(stateHolder, inputParser, eventManager, historyHolder)
}

internal fun provideInputParser(
    stateHolder: StateHolder = StateHolder(),
    gamesProvider: GamesProvider = GamesProvider()
) = InputParser(
    getActionGeneratorsSet(
        gamesProvider,
        ItemRoller(getItemFactoriesSet() as Set<WheelItem.Factory>),
    ) as Set<Action.Generator<*>>,
    stateHolder
)

private fun provideActionHandlerRegistry(gamesProvider: GamesProvider) = ActionHandlerRegistry(
    handlers = getActionHandlersMap(gamesProvider) as Map<Int, Action.Handler<*>>
)

private fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): EventHistoryHolder {
    return MockedLocalEventHistoryHolder(actionHandlerRegistry)
}
