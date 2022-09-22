package com.github.trueddd.core

import com.github.trueddd.data.GlobalState

interface ActionConsumer<A : Action> {

    suspend fun consume(action: A, currentState: GlobalState): GlobalState
}
