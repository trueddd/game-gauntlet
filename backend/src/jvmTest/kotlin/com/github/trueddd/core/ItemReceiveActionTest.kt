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
        val participant = requireRandomParticipant()
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.ItemReceive}:${WheelItem.Id.Plasticine.value}")
        assertEquals(expected = WheelItem.Id.Plasticine, inventoryOf(participant).firstOrNull()?.id)
    }

    @Test
    fun `roll pending event`() = runTest {
        val participant = requireRandomParticipant()
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.ItemReceive}:${WheelItem.Id.AwfulEvent.value}")
        assertEquals(expected = 0, inventoryOf(participant).size)
        assertEquals(expected = WheelItem.Id.AwfulEvent, pendingEventsOf(participant).firstOrNull()?.id)
    }
}
