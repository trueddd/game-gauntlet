package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.LoyalModerator
import com.github.trueddd.items.WeakThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoyalModeratorItemTest : EventGateTest() {

    @Test
    fun `use against the debuff`() = runTest {
        val user = requireRandomParticipant()
        val item = LoyalModerator.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        val debuff = WeakThrow.create()
        handleAction(ItemReceive(user, debuff))
        assertEquals(expected = 1, effectsOf(user).size)
        eventGate.parseAndHandle("${user.name}:4:${item.uid}:${debuff.uid}")
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 0, inventoryOf(user).size)
    }
}
