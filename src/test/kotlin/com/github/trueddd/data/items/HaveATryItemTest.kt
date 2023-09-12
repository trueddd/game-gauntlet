package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HaveATryItemTest : EventGateTest() {

    @Test
    fun `using item`() = runTest {
        val user = requireParticipant("shizov")
        val item = HaveATry.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).count { it is HaveATry })
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
    }
}
