package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import org.koin.core.annotation.Single

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

    private val actionsPipe = Channel<Action>()

    private var eventHandlingJob: Job? = null

    private val eventHandlingMonitor = Mutex(false)

    override suspend fun consumeAction(action: Action): EventManager.HandledAction {
        val result = handleAction(action)
        if (result.error == null) {
            Log.info(TAG, "Action(${action.id}) handled")
        } else {
            Log.info(TAG, "Action(${action.id}) handled with error: ${result.error}")
        }
        return result
    }

    override fun stopHandling() {
        if (eventHandlingJob?.isActive == true) {
            eventHandlingJob?.cancel()
            eventHandlingJob = null
        }
    }

    override fun startHandling(initState: GlobalState) {
        stateHolder.update { initState }
        startEventHandling()
    }

    private suspend fun handleAction(action: Action): EventManager.HandledAction {
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
        if (eventHandlingJob?.isActive == true) {
            Log.error(TAG, "EventManager is already running; skip start")
            return
        }
        eventHandlingJob = launch {
            Log.info(TAG, "Starting")
            for (action in actionsPipe) {
                handleAction(action)
            }
        }
    }
}
