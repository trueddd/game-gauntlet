package com.github.trueddd.core.history

import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.GlobalState

interface EventHistoryHolder {

    suspend fun pushEvent(action: Action)

    suspend fun save()

    suspend fun load(): GlobalState

    fun drop()
}
