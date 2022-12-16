package com.github.trueddd

import com.github.trueddd.core.*
import com.github.trueddd.core.history.LocalEventHistoryHolder
import com.github.trueddd.di.getActionConsumerMap
import com.github.trueddd.di.getActionGeneratorSet
import com.github.trueddd.di.getItemFactorySet

internal fun provideInputParser(stateHolder: StateHolder = StateHolder()) = InputParser(
    getActionGeneratorSet(
        ItemRoller(getItemFactorySet()),
        stateHolder,
    )
)

internal fun provideActionHandlerRegistry() = ActionHandlerRegistry(getActionConsumerMap())

internal fun provideHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry): LocalEventHistoryHolder {
    return LocalEventHistoryHolder(actionHandlerRegistry, ".\\src\\test\\resources\\history", overwrite = true)
}
