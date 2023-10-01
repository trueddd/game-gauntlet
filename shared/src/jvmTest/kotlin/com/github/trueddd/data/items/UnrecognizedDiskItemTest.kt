package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.UnrecognizedDisk
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnrecognizedDiskItemTest : EventGateTest() {

    @Test
    fun `single stream completion`() = runTest {
        val user = requireRandomParticipant()
        val item = UnrecognizedDisk.create()
        handleAction(ItemReceive(user, item))
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(ItemUse(user, item, "1"))
        assertEquals(expected = 1, stateOf(user).modifiersSum)
        assertTrue(pendingEventsOf(user).isEmpty())
    }

    @Test
    fun `not single stream completion`() = runTest {
        val user = requireRandomParticipant()
        val item = UnrecognizedDisk.create()
        handleAction(ItemReceive(user, item))
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(ItemUse(user, item, "0"))
        assertEquals(expected = 0, stateOf(user).modifiersSum)
        assertTrue(pendingEventsOf(user).isEmpty())
    }
}
