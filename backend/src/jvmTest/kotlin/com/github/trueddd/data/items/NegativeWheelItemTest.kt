package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.NegativeWheel
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue

class NegativeWheelItemTest : EventGateTest() {

    @Test
    fun `basic test`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, NegativeWheel.create()))
        assertTrue(effectsOf(user).isEmpty())
        assertTrue(inventoryOf(user).isEmpty())
        assertTrue(pendingEventsOf(user).isEmpty())
    }
}
