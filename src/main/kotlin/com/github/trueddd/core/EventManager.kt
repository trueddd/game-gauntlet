package com.github.trueddd.core

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class EventManager(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val eventHistoryHolder: EventHistoryHolder,
) : CoroutineScope {

    override val coroutineContext by lazy {
        SupervisorJob() + Dispatchers.Default
    }

    private val _globalStateFlow = MutableStateFlow(GlobalState.default())
    val globalStateFlow = _globalStateFlow.asStateFlow()

    private val actionsPipe = MutableSharedFlow<Action>()

    fun consumeAction(action: Action) {
        launch {
            actionsPipe.emit(action)
        }
    }

    fun save() {
        launch {
            eventHistoryHolder.save()
        }
    }

    private var eventHandlingJob: Job? = null

    suspend fun restore() {
        withContext(coroutineContext) {
            if (eventHandlingJob?.isActive == true) {
                eventHandlingJob?.cancel()
                eventHandlingJob = null
            }
            _globalStateFlow.value = eventHistoryHolder.load()
            startEventHandling()
        }
    }

    private fun startEventHandling() {
        if (eventHandlingJob?.isActive == true) {
            println("EventManager is already running; skip start")
            return
        }
        eventHandlingJob = actionsPipe
            .onStart { println("Starting EventManager") }
            .onEach { action ->
                val handler = actionHandlerRegistry.handlerOf(action) ?: return@onEach
                _globalStateFlow.update { handler.consume(action, it) }
                eventHistoryHolder.pushEvent(action)
            }
            .onCompletion { println("Finishing EventManager") }
            .launchIn(this)
    }

    init {
        startEventHandling()
    }
}
