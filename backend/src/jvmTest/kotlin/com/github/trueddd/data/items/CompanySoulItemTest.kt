package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.CompanySoul
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class CompanySoulItemTest : EventGateTest() {

    @Test
    fun `joke - corrupted name`() = runTest {
        val (joker, listener) = getPlayerNames()
        val item = CompanySoul.create()
        handleAction(ItemReceive(joker, item))
        assertEquals(expected = 1, pendingEventsOf(joker).size)
        assertFails { handleAction(ItemUse(joker, item.uid, listOf(listener.drop(1), "1"))) }
    }

    @Test
    fun `joke succeed`() = runTest {
        val (joker, listener) = getPlayerNames()
        val item = CompanySoul.create()
        handleAction(ItemReceive(joker, item))
        assertEquals(expected = 1, pendingEventsOf(joker).size)
        handleAction(ItemUse(joker, item.uid, listOf(listener, "1")))
        assertEquals(expected = 1, stateOf(joker).modifiersSum)
        assertEquals(expected = -1, stateOf(listener).modifiersSum)
        assertTrue(pendingEventsOf(joker).isEmpty())
    }

    @Test
    fun `joke failed`() = runTest {
        val (joker, listener) = getPlayerNames()
        val item = CompanySoul.create()
        handleAction(ItemReceive(joker, item))
        assertEquals(expected = 1, pendingEventsOf(joker).size)
        handleAction(ItemUse(joker, item.uid, listOf(listener, "0")))
        assertEquals(expected = -1, stateOf(joker).modifiersSum)
        assertEquals(expected = 1, stateOf(listener).modifiersSum)
        assertTrue(pendingEventsOf(joker).isEmpty())
    }
}
