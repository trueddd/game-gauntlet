package com.github.trueddd.core

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.handler.*
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ActionHandlerRegistry(
    @Named(ActionConsumer.TAG)
    private val handlers: Map<Int, ActionConsumer<*>>,
) {

    @Suppress("UNCHECKED_CAST")
    fun <A : Action> handlerOf(action: A): ActionConsumer<A>? {
        return handlers[action.id] as? ActionConsumer<A>
    }
}
