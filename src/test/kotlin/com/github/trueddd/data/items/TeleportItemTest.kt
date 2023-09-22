package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TeleportItemTest : EventGateTest() {

    @Test
    fun `no neighbors`() = runTest {
        val (user1, user2, user3) = requireParticipants()
        val item = Teleport.create()
        handleAction(BoardMove(user1, diceValue = 1))
        handleAction(BoardMove(user2, diceValue = 1))
        handleAction(BoardMove(user3, diceValue = 1))
        handleAction(ItemReceive(user1, item))
        assertEquals(expected = 1, pendingEventsOf(user1).size)
        handleAction(ItemUse(user1, item.uid))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        assertEquals(expected = 1, positionOf(user1))
    }

    @Test
    fun `1 neighbor`() = runTest {
        val (user1, user2, user3) = requireParticipants()
        val item = Teleport.create()
        handleAction(BoardMove(user1, diceValue = 1))
        handleAction(BoardMove(user2, diceValue = 1))
        handleAction(BoardMove(user3, diceValue = 4))
        handleAction(ItemReceive(user1, item))
        assertEquals(expected = 1, pendingEventsOf(user1).size)
        handleAction(ItemUse(user1, item.uid, listOf("0")))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        assertEquals(expected = 4, positionOf(user1))
    }

    @Test
    fun `2 neighbors - backward`() = runTest {
        val (user1, user2, user3, user4) = requireParticipants()
        val item = Teleport.create()
        handleAction(BoardMove(user1, diceValue = 2))
        handleAction(BoardMove(user2, diceValue = 1))
        handleAction(BoardMove(user4, diceValue = 2))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(ItemReceive(user1, item))
        assertEquals(expected = 1, pendingEventsOf(user1).size)
        handleAction(ItemUse(user1, item.uid, listOf("0")))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        assertEquals(expected = 1, positionOf(user1))
    }

    @Test
    fun `2 neighbors - forward`() = runTest {
        val (user1, user2, user3) = requireParticipants()
        val item = Teleport.create()
        handleAction(BoardMove(user1, diceValue = 2))
        handleAction(BoardMove(user2, diceValue = 1))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(ItemReceive(user1, item))
        assertEquals(expected = 1, pendingEventsOf(user1).size)
        handleAction(ItemUse(user1, item.uid, listOf("1")))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        assertEquals(expected = 3, positionOf(user1))
    }

    @Test
    fun `mandatory argument`() = runTest {
        val (user1, user2, user3) = requireParticipants()
        val item = Teleport.create()
        handleAction(BoardMove(user1, diceValue = 2))
        handleAction(BoardMove(user2, diceValue = 1))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(ItemReceive(user1, item))
        assertEquals(expected = 1, pendingEventsOf(user1).size)
        handleAction(ItemUse(user1, item.uid))
        assertEquals(expected = 1, pendingEventsOf(user1).size)
    }
}
