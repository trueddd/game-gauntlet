package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertEquals

class StateRecoverability : EventGateTest() {

    @RepeatedTest(10)
    fun `save, load & compare`() = runTest {
        val actionsSequence = sequenceOf(
            "shizov:${Action.Key.BoardMove}",
            "shizov:${Action.Key.GameRoll}",
            "solll:${Action.Key.BoardMove}",
            "shizov:${Action.Key.ItemReceive}",
            "shizov:${Action.Key.GameDrop}",
            "shizov:${Action.Key.GameRoll}",
            "shizov:${Action.Key.GameStatusChange}:1",
            "shizov:${Action.Key.BoardMove}",
            "keli:${Action.Key.BoardMove}",
        )
        actionsSequence.forEach {
            eventGate.parseAndHandleSuspend(it)
        }
        eventGate.historyHolder.save(eventGate.stateHolder.current)
        eventGate.eventManager.stopHandling()
        val restored = eventGate.historyHolder.load()
        assertEquals(eventGate.stateHolder.globalStateFlow.value, restored)
    }
}
