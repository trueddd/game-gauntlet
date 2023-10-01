package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.di.getItemFactoriesSet
import com.github.trueddd.items.ClimbingRope
import com.github.trueddd.items.Poll
import com.github.trueddd.items.PowerThrow
import com.github.trueddd.items.WheelItem
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PollItemTest : EventGateTest() {

    @Test
    fun `take item for themselves`() = runTest {
        val user = requireRandomParticipant()
        val poll = Poll.create()
        handleAction(ItemReceive(user, poll))
        val factory = getItemFactoriesSet().first { it.itemId == WheelItem.Id.ClimbingRope }
        handleAction(ItemUse(user, poll, factory.itemId.asString(), user.name))
        assertEquals(factory.itemId, inventoryOf(user).first().id)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `pass item to another player`() = runTest {
        val (user1, user2) = requireParticipants()
        val poll = Poll.create()
        handleAction(ItemReceive(user1, poll))
        handleAction(ItemReceive(user2, PowerThrow.create()))
        handleAction(ItemReceive(user2, ClimbingRope.create()))
        val factory = getItemFactoriesSet().first { it.itemId == WheelItem.Id.HoleyPockets }
        handleAction(ItemUse(user1, poll, factory.itemId.asString(), user2.name))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        assertTrue(inventoryOf(user2).isEmpty())
        assertTrue(effectsOf(user2).isEmpty())
    }
}
