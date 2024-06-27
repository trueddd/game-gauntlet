package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.Action
import com.github.trueddd.items.WheelItem
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ItemReceiveActionTest : EventGateTest() {

    @Test
    fun `roll plasticine`() = runTest {
        val participant = getRandomPlayerName()
        eventGate.parseAndHandle("$participant:${Action.Key.ItemReceive}:${WheelItem.Plasticine}")
        assertEquals(expected = WheelItem.Id(WheelItem.Plasticine), inventoryOf(participant).firstOrNull()?.id)
    }

    @Test
    fun `roll pending event`() = runTest {
        val participant = getRandomPlayerName()
        eventGate.parseAndHandle("$participant:${Action.Key.ItemReceive}:${WheelItem.AwfulEvent}")
        assertEquals(expected = 0, inventoryOf(participant).size)
        assertEquals(expected = WheelItem.Id(WheelItem.AwfulEvent), pendingEventsOf(participant).firstOrNull()?.id)
    }
}
