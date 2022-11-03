package com.github.trueddd.core

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.handler.*
import org.koin.core.annotation.Single

@Single
class ActionHandlerRegistry {

    // TODO: provide list of handlers using annotations
    private val handlers = mapOf(
        Action.Keys.BoardMove to PlayerBoardMoveHandler(),
        Action.Keys.GameDrop to GameDropHandler(),
        Action.Keys.ItemReceive to ItemReceiveHandler(),
    )

    @Suppress("UNCHECKED_CAST")
    fun <A : Action> handlerOf(action: A): ActionConsumer<A>? {
        return handlers[action.id] as? ActionConsumer<A>
    }
}
