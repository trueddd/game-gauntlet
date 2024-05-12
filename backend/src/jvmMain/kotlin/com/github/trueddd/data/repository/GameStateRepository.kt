package com.github.trueddd.data.repository

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.model.SavedState

interface GameStateRepository {
    suspend fun save(globalState: GlobalState, actions: List<Action>)
    suspend fun load(): SavedState
}
