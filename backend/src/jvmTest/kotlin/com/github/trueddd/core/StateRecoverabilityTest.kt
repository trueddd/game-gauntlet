package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertEquals

class StateRecoverabilityTest : EventGateTest() {

    @RepeatedTest(50)
    fun `save, load & compare`() = runTest {
        val (user1, user2, user3) = requireParticipants()
        val actionsSequence = sequenceOf(
            "${user1.name}:${Action.Key.BoardMove}",
            "${user1.name}:${Action.Key.GameRoll}",
            "${user2.name}:${Action.Key.BoardMove}",
            "${user1.name}:${Action.Key.ItemReceive}",
            "${user1.name}:${Action.Key.GameDrop}",
            "${user1.name}:${Action.Key.GameRoll}",
            "${user1.name}:${Action.Key.GameStatusChange}:1",
            "${user1.name}:${Action.Key.BoardMove}",
            "${user3.name}:${Action.Key.BoardMove}",
        )
        actionsSequence.forEach {
            eventGate.parseAndHandle(it)
            // This delay is needed to avoid similar time for different actions to be compared
            // in game loading process. `withContext` is here to ensure that delay will be not skipped.
            withContext(Dispatchers.Default) {
                delay(2L)
            }
        }
        eventGate.historyHolder.save(eventGate.stateHolder.current)
        eventGate.eventManager.stopHandling()
        val restored = eventGate.historyHolder.load()
        assertEquals(eventGate.stateHolder.current, restored.globalState)
    }
}
