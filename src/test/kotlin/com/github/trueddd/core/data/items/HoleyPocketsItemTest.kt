package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.items.HoleyPockets
import com.github.trueddd.data.items.LoyalModerator
import com.github.trueddd.data.items.PowerThrow
import com.github.trueddd.data.items.Viewer
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HoleyPocketsItemTest : EventGateTest() {

    @Test
    fun `use event with inventory and effects`() = runTest {
        val user = requireParticipant("shizov")
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(ItemReceive(user, LoyalModerator.create()))
        handleAction(ItemReceive(user, Viewer.create()))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 2, effectsOf(user).size)
        handleAction(ItemReceive(user, HoleyPockets.create()))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
    }

    @Test
    fun `use event on empty inventory`() = runTest {
        val user = requireParticipant("shizov")
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(ItemReceive(user, HoleyPockets.create()))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
