package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState

interface ActionConsumer<A : Action> {

    suspend fun consume(action: A, currentState: GlobalState): GlobalState
}
