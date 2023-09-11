package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.items.Reroll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RerollItemTest : EventGateTest() {

    @Test
    fun `test reroll use`() = runTest {
        val user = requireParticipant("shizov")
        val initial = eventGate.stateHolder.current
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, Reroll.create()))
        assertEquals(expected = initial, eventGate.stateHolder.current)
    }
}
