package com.github.trueddd.core.handler

import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.GlobalState

interface ActionConsumer<A : Action> {

    companion object {
        const val TAG = "ActionConsumer"
    }

    suspend fun consume(action: A, currentState: GlobalState): GlobalState
}
