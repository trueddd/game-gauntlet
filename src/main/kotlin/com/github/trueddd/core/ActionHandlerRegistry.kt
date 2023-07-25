package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ActionHandlerRegistry(
    @Named(Action.Handler.MapTag)
    private val handlers: Map<Int, Action.Handler<*>>,
) {

    @Suppress("UNCHECKED_CAST")
    fun <A : Action> handlerOf(action: A): Action.Handler<A>? {
        return handlers[action.id] as? Action.Handler<A>
    }
}
