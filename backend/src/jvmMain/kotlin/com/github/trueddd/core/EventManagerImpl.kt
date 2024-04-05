package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.util.concurrent.atomic.AtomicBoolean

@Single
class EventManagerImpl(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val stateHolder: StateHolderImpl,
) : EventManager, CoroutineScope {

    companion object {
        const val TAG = "EventManager"
    }

    override val coroutineContext by lazy {
        SupervisorJob() + Dispatchers.Default
    }

    private val isEnabled = AtomicBoolean(false)

    private val eventHandlingMonitor = Mutex(false)

    override suspend fun consumeAction(action: Action): EventManager.HandledAction {
        val result = withContext(coroutineContext) { handleAction(action) }
        if (result.error == null) {
            Log.info(TAG, "Action(${action.id}) handled")
        } else {
            Log.info(TAG, "Action(${action.id}) handled with error: ${result.error}")
        }
        return result
    }

    override fun stopHandling() {
        isEnabled.set(false)
    }

    override fun startHandling(initState: GlobalState) {
        stateHolder.update { initState }
        startEventHandling()
    }

    private suspend fun handleAction(action: Action): EventManager.HandledAction {
        if (!isEnabled.get()) {
            return EventManager.HandledAction.from(
                action,
                StateModificationException(action, "EventManager is not running")
            )
        }
        if (action.issuedAt >= stateHolder.current.endDate) {
            return EventManager.HandledAction.from(
                action,
                StateModificationException(action, "Game is over")
            )
        }
        if (action.issuedAt < stateHolder.current.startDate) {
            return EventManager.HandledAction.from(
                action,
                StateModificationException(action, "Game is not started yet")
            )
        }
        val handler = actionHandlerRegistry.handlerOf(action)
            ?: return EventManager.HandledAction.from(
                action,
                StateModificationException(action, "No suitable handler found for action: $action")
            )
        eventHandlingMonitor.lock()
        return try {
            val result = handler.handle(action, stateHolder.current)
            stateHolder.update { result }
            EventManager.HandledAction(action.id, action.issuedAt)
        } catch (error: Exception) {
            EventManager.HandledAction(action.id, action.issuedAt, error)
        } finally {
            eventHandlingMonitor.unlock()
        }
    }

    private fun startEventHandling() {
        if (isEnabled.get()) {
            Log.error(TAG, "EventManager is already running; skip start")
            return
        }
        Log.info(TAG, "Starting")
        isEnabled.set(true)
    }
}
