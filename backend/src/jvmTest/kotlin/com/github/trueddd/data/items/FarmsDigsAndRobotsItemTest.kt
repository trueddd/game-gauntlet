package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.FarmsDigsAndRobots
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue

class FarmsDigsAndRobotsItemTest : EventGateTest() {

    @Test
    fun `use item`() = runTest {
        val user = requireRandomParticipant()
        val item = FarmsDigsAndRobots.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid))
        assertTrue(pendingEventsOf(user).isEmpty())
    }
}
