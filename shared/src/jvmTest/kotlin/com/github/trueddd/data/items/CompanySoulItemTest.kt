package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.CompanySoul
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompanySoulItemTest : EventGateTest() {

    @Test
    fun `joke - corrupted name`() = runTest {
        val (joker, listener) = requireParticipants()
        val item = CompanySoul.create()
        handleAction(ItemReceive(joker, item))
        assertEquals(expected = 1, pendingEventsOf(joker).size)
        handleAction(ItemUse(joker, item.uid, listOf(listener.name.drop(1), "1")))
        assertEquals(expected = 0, stateOf(joker).modifiersSum)
        assertEquals(expected = 0, stateOf(listener).modifiersSum)
        assertFalse(pendingEventsOf(joker).isEmpty())
    }

    @Test
    fun `joke succeed`() = runTest {
        val (joker, listener) = requireParticipants()
        val item = CompanySoul.create()
        handleAction(ItemReceive(joker, item))
        assertEquals(expected = 1, pendingEventsOf(joker).size)
        handleAction(ItemUse(joker, item.uid, listOf(listener.name, "1")))
        assertEquals(expected = 1, stateOf(joker).modifiersSum)
        assertEquals(expected = -1, stateOf(listener).modifiersSum)
        assertTrue(pendingEventsOf(joker).isEmpty())
    }

    @Test
    fun `joke failed`() = runTest {
        val (joker, listener) = requireParticipants()
        val item = CompanySoul.create()
        handleAction(ItemReceive(joker, item))
        assertEquals(expected = 1, pendingEventsOf(joker).size)
        handleAction(ItemUse(joker, item.uid, listOf(listener.name, "0")))
        assertEquals(expected = -1, stateOf(joker).modifiersSum)
        assertEquals(expected = 1, stateOf(listener).modifiersSum)
        assertTrue(pendingEventsOf(joker).isEmpty())
    }
}
