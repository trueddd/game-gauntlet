package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState

interface EventHistoryHolder {

    suspend fun pushEvent(action: Action)

    suspend fun save(globalState: GlobalState)

    suspend fun load(): GlobalState

    fun drop()
}
