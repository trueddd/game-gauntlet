package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import org.jetbrains.annotations.TestOnly
import org.koin.core.annotation.Single

/**
 * Event manager is a main component of the system.
 * Flow of handling game events:
 * 1. Parsing input commands and generating an action out of input with an [action generator][com.github.trueddd.core.actions.Action.Generator].
 * 2. Event manager looks up the [handler][com.github.trueddd.core.actions.Action.Handler] for the generated action.
 * 3. Found handler mutates the current state.
 * 4. Applied action is recorded to the [event history holder][com.github.trueddd.core.history.EventHistoryHolder],
 * so whole state can be recreated later after server reboot.
 */
@Single
class EventManager(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val stateHolder: StateHolder,
) : CoroutineScope {

    companion object {
        const val TAG = "EventManager"
    }

    override val coroutineContext by lazy {
        SupervisorJob() + Dispatchers.Default
    }

    private val actionsPipe = MutableSharedFlow<Action>()

    private var eventHandlingJob: Job? = null

    private val eventHandlingMonitor = Mutex(false)

    private val handledActionsFlow = MutableSharedFlow<Pair<Int, Exception?>>()

    init {
        startEventHandling()
    }

    fun consumeAction(action: Action) {
        launch {
            Log.info(TAG, "Consuming action: $action")
            actionsPipe.emit(action)
        }
    }

    @Suppress("SuspendFunctionOnCoroutineScope")
    @TestOnly
    suspend fun suspendConsumeAction(action: Action) {
        handledActionsFlow
            .onStart {
                this@EventManager.launch {
                    Log.info(TAG, "Consuming action: $action")
                    actionsPipe.emit(action)
                }
            }
            .filter { (id, error) -> id == action.id }
            .take(1)
            .collect { Log.info(TAG, "Action(${action.id}) consumed") }
    }

    fun stopHandling() {
        if (eventHandlingJob?.isActive == true) {
            eventHandlingJob?.cancel()
            eventHandlingJob = null
        }
    }

    fun startHandling(initState: GlobalState = GlobalState.default()) {
        stateHolder.update { initState }
        startEventHandling()
    }

    private fun startEventHandling() {
        if (eventHandlingJob?.isActive == true) {
            Log.error(TAG, "EventManager is already running; skip start")
            return
        }
        eventHandlingJob = actionsPipe
            .onStart { Log.info(TAG, "Starting") }
            .onEach { action ->
                val handler = actionHandlerRegistry.handlerOf(action) ?: return@onEach
                eventHandlingMonitor.lock()
                try {
                    val result = handler.handle(action, stateHolder.globalStateFlow.value)
                    stateHolder.update { result }
                    handledActionsFlow.emit(action.id to null)
                } catch (error: StateModificationException) {
                    handledActionsFlow.emit(action.id to error)
                } finally {
                    eventHandlingMonitor.unlock()
                }
            }
            .onCompletion { Log.info(TAG, "Finishing") }
            .launchIn(this)
    }
}
