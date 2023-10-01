package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RatMoveItemTest : EventGateTest() {

    @Test
    fun `acquire item`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, RatMove.create()))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        assertIs<RatMove>(pendingEventsOf(user).first())
    }

    @Test
    fun `use pending event - drop inventory`() = runTest {
        val (user1, user2) = requireParticipants()
        val ratMoveItem = RatMove.create()
        val plasticineItem = Plasticine.create()
        handleAction(ItemReceive(user1, ratMoveItem))
        handleAction(ItemReceive(user2, plasticineItem))
        assertIs<Plasticine>(inventoryOf(user2).first())
        handleAction(ItemUse(user1, ratMoveItem.uid, listOf(user2.name)))
        assertTrue(pendingEventsOf(user1).isEmpty())
        assertTrue(inventoryOf(user2).isEmpty())
    }

    @Test
    fun `use pending event - drop inventory and effects`() = runTest {
        val (user1, user2) = requireParticipants()
        val ratMoveItem = RatMove.create()
        handleAction(ItemReceive(user1, ratMoveItem))
        handleAction(ItemReceive(user2, Plasticine.create()))
        handleAction(ItemReceive(user2, PowerThrow.create()))
        handleAction(ItemReceive(user2, Viewer.create()))
        assertIs<Plasticine>(inventoryOf(user2).first())
        assertEquals(expected = 2, effectsOf(user2).size)
        assertEquals(expected = 1, effectsOf(user2).filterIsInstance<WheelItem.Effect.Buff>().size)
        assertEquals(expected = 1, effectsOf(user2).filterIsInstance<WheelItem.Effect.Debuff>().size)
        handleAction(ItemUse(user1, ratMoveItem.uid, listOf(user2.name)))
        assertTrue(pendingEventsOf(user1).isEmpty())
        assertTrue(inventoryOf(user2).isEmpty())
        assertTrue(effectsOf(user2).isEmpty())
    }
}
