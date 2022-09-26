package com.github.trueddd.core

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.handler.ActionConsumer
import com.github.trueddd.core.handler.GameDropHandler
import com.github.trueddd.core.handler.PlayerBoardMoveHandler

class ActionHandlerRegistry {

    private val handlers = mapOf(
        Action.Keys.BoardMove to PlayerBoardMoveHandler(),
        Action.Keys.GameDrop to GameDropHandler(),
    )

    @Suppress("UNCHECKED_CAST")
    fun <A : Action> handlerOf(action: A): ActionConsumer<A>? {
        return handlers[action.id] as? ActionConsumer<A>
    }
}
