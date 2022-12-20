package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.core.history.MockedLocalEventHistoryHolder
import com.github.trueddd.di.getActionConsumerMap
import com.github.trueddd.di.getActionGeneratorSet
import com.github.trueddd.di.getItemFactorySet

internal fun provideEventGate(): EventGate {
    val stateHolder = StateHolder()
    val actionHandlerRegistry = provideActionHandlerRegistry()
    val inputParser = provideInputParser()
    val eventManager = EventManager(actionHandlerRegistry, stateHolder)
    val historyHolder = provideHistoryHolder(actionHandlerRegistry)
    return EventGate(stateHolder, inputParser, eventManager, historyHolder)
}

internal fun provideInputParser() = InputParser(
    getActionGeneratorSet(
        ItemRoller(getItemFactorySet()),
    )
)

private fun provideActionHandlerRegistry() = ActionHandlerRegistry(getActionConsumerMap())

private fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): EventHistoryHolder {
    return MockedLocalEventHistoryHolder(actionHandlerRegistry)
}
