package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.history.LocalEventHistoryHolder
import com.github.trueddd.core.history.MockedLocalEventHistoryHolder
import com.github.trueddd.di.getActionConsumerMap
import com.github.trueddd.di.getActionGeneratorSet
import com.github.trueddd.di.getItemFactorySet

internal fun provideInputParser() = InputParser(
    getActionGeneratorSet(
        ItemRoller(getItemFactorySet()),
    )
)

internal fun provideActionHandlerRegistry() = ActionHandlerRegistry(getActionConsumerMap())

internal fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): LocalEventHistoryHolder {
    return MockedLocalEventHistoryHolder(actionHandlerRegistry)
}
