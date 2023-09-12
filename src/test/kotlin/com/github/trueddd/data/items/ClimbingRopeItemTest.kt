package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.*
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ClimbingRopeItemTest : EventGateTest() {

    @Test
    fun `drop game with item`() = runTest {
        val user = requireParticipant("shizov")
        val item = ClimbingRope.create()
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 1, effectsOf(user).size)
        eventGate.eventManager.suspendConsumeAction(GameDrop(user, 4))
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 4, positionOf(user))
    }

    @Test
    fun `drop game with no item`() = runTest {
        val user = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        assertEquals(expected = 0, effectsOf(user).size)
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameDrop(user, 4))
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 1, positionOf(user))
    }

    @Test
    fun `use item and do not drop`() = runTest {
        val user = requireParticipant("shizov")
        val item = ClimbingRope.create()
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 1, effectsOf(user).size)
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
