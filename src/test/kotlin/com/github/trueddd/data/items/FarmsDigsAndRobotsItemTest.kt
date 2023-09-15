package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
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
