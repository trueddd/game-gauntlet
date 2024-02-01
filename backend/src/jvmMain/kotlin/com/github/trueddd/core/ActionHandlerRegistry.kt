package com.github.trueddd.core

import com.github.trueddd.actions.Action

interface ActionHandlerRegistry {

    fun <A : Action> handlerOf(action: A): Action.Handler<A>?
}
