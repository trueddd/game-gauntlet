package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.items.LoyalModerator
import com.github.trueddd.data.items.WeakThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoyalModeratorItemTest : EventGateTest() {

    @Test
    fun `use against the debuff`() = runTest {
        val user = requireParticipant("shizov")
        val item = LoyalModerator.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        val debuff = WeakThrow.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, debuff))
        assertEquals(expected = 1, effectsOf(user).size)
        eventGate.parseAndHandleSuspend("shizov:4:${item.uid}:${debuff.uid}")
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 0, inventoryOf(user).size)
    }
}
