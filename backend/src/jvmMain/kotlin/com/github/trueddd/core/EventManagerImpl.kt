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

    private suspend fun sendAction(action: Action) {
        Log.info(TAG, "Consuming action: $action")
        actionsPipe.send(action)
    }

    override fun consumeAction(action: Action) {
        launch {
            sendAction(action)
        }
    }

    override suspend fun suspendConsumeAction(action: Action): EventManager.HandledAction {
        return handleAction(action).also { Log.info(TAG, "Action(${action.id}) handled") }
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
        val handler = actionHandlerRegistry.handlerOf(action)
            ?: return EventManager.HandledAction(
                action.id,
                action.issuedAt,
                StateModificationException(action, "No suitable handler found for action: $action")
            )
        eventHandlingMonitor.lock()
        return try {
            val result = handler.handle(action, stateHolder.globalStateFlow.value)
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
