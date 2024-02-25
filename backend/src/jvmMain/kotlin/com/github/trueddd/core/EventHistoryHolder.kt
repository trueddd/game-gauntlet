package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.flow.Flow

interface EventHistoryHolder {

    val actionsChannel: Flow<Action>

    suspend fun getActions(): List<Action>

    suspend fun pushEvent(action: Action)

    suspend fun save(globalState: GlobalState)

    suspend fun load(): GlobalState

    fun drop()
}
