package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.NimbleFingers
import com.github.trueddd.items.Plasticine
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals

class NimbleFingersItemTest : EventGateTest() {

    @Test
    fun `nothing to steal`() = runTest {
        val (user1) = requireParticipants()
        val nimbleFingers = NimbleFingers.create()
        handleAction(ItemReceive(user1, nimbleFingers))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
    }

    @Test
    fun `steal - success`() = runTest {
        val (user1, user2) = requireParticipants()
        val nimbleFingers = NimbleFingers.create()
        val plasticine = Plasticine.create()
        handleAction(ItemReceive(user2, plasticine))
        handleAction(ItemReceive(user1, nimbleFingers))
        handleAction(ItemUse(user1, nimbleFingers, user2.name, plasticine.uid))
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        assertEquals(expected = 0, inventoryOf(user2).size)
        assertEquals(expected = 1, inventoryOf(user1).size)
    }
}
