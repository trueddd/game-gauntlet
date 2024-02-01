package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.HoleyPockets
import com.github.trueddd.items.LoyalModerator
import com.github.trueddd.items.PowerThrow
import com.github.trueddd.items.Viewer
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HoleyPocketsItemTest : EventGateTest() {

    @Test
    fun `use event with inventory and effects`() = runTest {
        val user = requireRandomParticipant()
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
        val user = requireRandomParticipant()
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(ItemReceive(user, HoleyPockets.create()))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
