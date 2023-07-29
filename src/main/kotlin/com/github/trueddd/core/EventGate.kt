package com.github.trueddd.core

import com.github.trueddd.core.history.EventHistoryHolder
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.Log
import org.jetbrains.annotations.TestOnly
import org.koin.core.annotation.Single

@Single
class EventGate(
    val stateHolder: StateHolder,
    private val inputParser: InputParser,
    val eventManager: EventManager,
    val historyHolder: EventHistoryHolder,
) {

    companion object {
        private const val TAG = "EventGate"
    }

    suspend fun parseAndHandle(input: String): Boolean {
        val action = inputParser.parse(input) ?: return false
        eventManager.consumeAction(action)
        historyHolder.pushEvent(action)
        return true
    }

    @TestOnly
    suspend fun parseAndHandleSuspend(input: String): Boolean {
        val action = inputParser.parse(input) ?: return false
        eventManager.suspendConsumeAction(action)
        historyHolder.pushEvent(action)
        return true
    }

    fun start() {
        Log.info(TAG, "Starting...")
        eventManager.startHandling()
    }

    fun stop() {
        Log.info(TAG, "Stopping and clearing...")
        eventManager.stopHandling()
        stateHolder.update { GlobalState.default() }
        historyHolder.drop()
    }
}
