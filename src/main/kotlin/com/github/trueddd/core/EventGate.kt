package com.github.trueddd.core

import com.github.trueddd.core.history.EventHistoryHolder
import org.jetbrains.annotations.TestOnly
import org.koin.core.annotation.Single

@Single
class EventGate(
    val stateHolder: StateHolder,
    private val inputParser: InputParser,
    val eventManager: EventManager,
    val historyHolder: EventHistoryHolder,
) {

    suspend fun parseAndHandle(input: String): Boolean {
        val action = inputParser.parse(input) ?: return false
        eventManager.consumeAction(action)
        if (!action.singleShot) {
            historyHolder.pushEvent(action)
        }
        return true
    }

    @TestOnly
    suspend fun parseAndHandleSuspend(input: String): Boolean {
        val action = inputParser.parse(input) ?: return false
        eventManager.suspendConsumeAction(action)
        if (!action.singleShot) {
            historyHolder.pushEvent(action)
        }
        return true
    }
}
