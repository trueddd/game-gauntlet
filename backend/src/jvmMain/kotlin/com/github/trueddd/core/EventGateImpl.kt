package com.github.trueddd.core

import com.github.trueddd.data.globalState
import com.github.trueddd.utils.Log
import org.koin.core.annotation.Single

@Single
class EventGateImpl(
    override val stateHolder: StateHolderImpl,
    private val inputParser: InputParser,
    override val eventManager: EventManager,
    override val historyHolder: EventHistoryHolder,
) : EventGate {

    companion object {
        private const val TAG = "EventGate"
    }

    override fun getInputParser() = inputParser

    override suspend fun parseAndHandle(input: String): Boolean {
        val action = inputParser.parse(input) ?: return false
        eventManager.consumeAction(action)
        historyHolder.pushEvent(action)
        return true
    }

    override suspend fun parseAndHandleSuspend(input: String): Boolean {
        val action = inputParser.parse(input) ?: return false
        eventManager.suspendConsumeAction(action)
        historyHolder.pushEvent(action)
        return true
    }

    override fun startNoLoad() {
        eventManager.startHandling()
    }

    override suspend fun start() {
        Log.info(TAG, "Starting...")
        val savedHistory = historyHolder.load()
        eventManager.startHandling(savedHistory)
    }

    override fun stop() {
        Log.info(TAG, "Stopping and clearing...")
        eventManager.stopHandling()
        stateHolder.update { globalState() }
        historyHolder.drop()
    }
}
