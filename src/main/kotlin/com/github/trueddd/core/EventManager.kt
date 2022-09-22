package com.github.trueddd.core

import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EventManager : CoroutineScope {

    override val coroutineContext by lazy {
        SupervisorJob() + Dispatchers.Default
    }

    private val _globalStateFlow = MutableStateFlow(GlobalState.default())
    val globalStateFlow = _globalStateFlow.asStateFlow()

    private val actionsPipe = MutableSharedFlow<Action>()

    private val actionHandlerRegistry by lazy {
        ActionHandlerRegistry()
    }

    fun consumeAction(action: Action) {
        launch {
            actionsPipe.emit(action)
        }
    }

    init {
        println("Starting EventManager")
        actionsPipe
            .onEach { action ->
                val handler = actionHandlerRegistry.handlerOf(action) ?: return@onEach
                _globalStateFlow.update { handler.consume(action, it) }
            }
            .launchIn(this)
    }
}
