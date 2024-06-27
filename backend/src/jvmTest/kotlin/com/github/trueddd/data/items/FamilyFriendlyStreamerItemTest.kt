package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.FamilyFriendlyStreamer
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FamilyFriendlyStreamerItemTest : EventGateTest() {

    @Test
    fun `get item - complete game successfully`() = runTest {
        val user = getRandomPlayerName()
        val item = FamilyFriendlyStreamer.create()
        handleAction(ItemReceive(user, item))
        assertTrue(pendingEventsOf(user).isNotEmpty())
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(pendingEventsOf(user).isEmpty())
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `get item - use item`() = runTest {
        val user = getRandomPlayerName()
        val item = FamilyFriendlyStreamer.create()
        handleAction(ItemReceive(user, item))
        assertTrue(pendingEventsOf(user).isNotEmpty())
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemUse(user, item.uid))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(pendingEventsOf(user).isEmpty())
        assertTrue(effectsOf(user).isNotEmpty())
        assertEquals(expected = -3, stateOf(user).modifiersSum)
    }
}
