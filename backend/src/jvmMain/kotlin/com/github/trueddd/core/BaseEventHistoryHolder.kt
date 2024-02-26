package com.github.trueddd.core

import com.github.trueddd.actions.Action
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

abstract class BaseEventHistoryHolder : EventHistoryHolder {

    protected val mutex = Mutex(locked = false)

    protected val latestEvents = LinkedList<Action>()

    override val actionsChannel = MutableSharedFlow<Action>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun getActions(): List<Action> {
        return mutex.withLock { latestEvents.toList() }
    }

    override suspend fun pushEvent(action: Action) {
        mutex.withLock { latestEvents.push(action) }
        actionsChannel.emit(action)
    }

    override fun drop() {
        latestEvents.clear()
    }
}
