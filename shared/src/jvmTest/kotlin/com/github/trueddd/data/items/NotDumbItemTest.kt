package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NotDumbItemTest : EventGateTest() {

    @Test
    fun `get item`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, NotDumb.create()))
        assertEquals(expected = 1, pendingEventsOf(user).size)
    }

    @Test
    fun `result - 0 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("0")))
        assertEquals(expected = -3, stateOf(user).modifiersSum)
    }

    @Test
    fun `result - 2 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("2")))
        assertEquals(expected = -3, stateOf(user).modifiersSum)
    }

    @Test
    fun `result - 3 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("3")))
        assertEquals(expected = -2, stateOf(user).modifiersSum)
    }

    @Test
    fun `result - 4 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("4")))
        assertEquals(expected = -1, stateOf(user).modifiersSum)
    }

    @Test
    fun `result - 5 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("5")))
        assertEquals(expected = 1, stateOf(user).modifiersSum)
    }

    @Test
    fun `result - 6 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("6")))
        assertEquals(expected = 2, stateOf(user).modifiersSum)
    }

    @Test
    fun `result - 7 out of 7`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("7")))
        assertEquals(expected = 3, stateOf(user).modifiersSum)
    }

    @Test
    fun `make a move after result`() = runTest {
        val user = requireRandomParticipant()
        val item = NotDumb.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("7")))
        assertEquals(expected = 3, stateOf(user).modifiersSum)
        assertEquals(expected = 0, pendingEventsOf(user).size)
        handleAction(BoardMove(user, diceValue = 3))
        assertEquals(expected = 6, positionOf(user))
        assertEquals(expected = 0, stateOf(user).modifiersSum)
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
