package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
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

    private val handledActionsFlow = MutableSharedFlow<EventManager.HandledAction>()

    // todo: make it explicit
    init {
        startEventHandling()
    }

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
        val handledAction = handledActionsFlow
            .onSubscription { sendAction(action) }
            .filter { (id, issuedAt, _) -> id == action.id && issuedAt == action.issuedAt }
            .first()
        Log.info(TAG, "Action(${action.id}) handled")
        return handledAction
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

    private fun startEventHandling() {
        if (eventHandlingJob?.isActive == true) {
            Log.error(TAG, "EventManager is already running; skip start")
            return
        }
        eventHandlingJob = launch {
            Log.info(TAG, "Starting")
            for (action in actionsPipe) {
                val handler = actionHandlerRegistry.handlerOf(action) ?: continue
                eventHandlingMonitor.lock()
                try {
                    val result = handler.handle(action, stateHolder.globalStateFlow.value)
                    stateHolder.update { result }
                    handledActionsFlow.emit(EventManager.HandledAction(action.id, action.issuedAt))
                } catch (error: Exception) {
                    handledActionsFlow.emit(EventManager.HandledAction(action.id, action.issuedAt, error))
                } finally {
                    eventHandlingMonitor.unlock()
                }
            }
        }
    }
}
