package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.trueddd.github.annotations.ActionHandler
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ActionHandlerRegistryImpl(
    @Named(ActionHandler.TAG)
    private val handlers: Map<Int, Action.Handler<*>>,
) : ActionHandlerRegistry {

    @Suppress("UNCHECKED_CAST")
    override fun <A : Action> handlerOf(action: A): Action.Handler<A>? {
        return handlers[action.id] as? Action.Handler<A>
    }
}
