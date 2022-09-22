package com.github.trueddd.core

import com.github.trueddd.core.handler.GameDropHandler
import com.github.trueddd.core.handler.PlayerForwardMoveHandler

class ActionHandlerRegistry {

    private val handlers = mapOf<Int, ActionConsumer<out Action>>(
        2 to PlayerForwardMoveHandler(),
        3 to GameDropHandler(),
    )

    fun <A : Action> handlerOf(action: A): ActionConsumer<A>? {
        return handlers[action.id] as? ActionConsumer<A>
    }
}
