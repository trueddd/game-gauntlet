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

    suspend fun parseAndHandle(input: String) {
        inputParser.parse(input)?.let {
            eventManager.consumeAction(it)
            if (!it.singleShot) {
                historyHolder.pushEvent(it)
            }
        }
    }

    @TestOnly
    suspend fun parseAndHandleSuspend(input: String) {
        inputParser.parse(input)?.let {
            eventManager.suspendConsumeAction(it)
            if (!it.singleShot) {
                historyHolder.pushEvent(it)
            }
        }
    }
}
