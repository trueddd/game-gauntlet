package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayersHistory
import com.github.trueddd.di.CoroutineDispatchers
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

@Single
class EventManagerImpl(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val stateHolder: StateHolder,
    dispatchers: CoroutineDispatchers,
) : EventManager {

    companion object {
        const val TAG = "EventManager"
    }

    private val coroutineContext: CoroutineContext = SupervisorJob() + dispatchers.default

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

    override fun startHandling(initState: GlobalState, playersHistory: PlayersHistory) {
        stateHolder.update { initState }
        stateHolder.updateHistory { playersHistory }
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
            val oldState = stateHolder.current
            val result = handler.handle(action, oldState)
            stateHolder.update { result }
            stateHolder.updateHistory {
                PlayersHistoryCalculator.calculate(
                    currentHistory = stateHolder.currentPlayersHistory,
                    action = action,
                    oldState = oldState,
                    newState = stateHolder.current
                )
            }
            EventManager.HandledAction(action.id, action.issuedAt)
        } catch (error: StateModificationException) {
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
