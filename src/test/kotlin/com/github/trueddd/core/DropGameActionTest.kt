package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.data.items.DropReverse
import com.github.trueddd.data.items.SamuraiLunge
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DropGameActionTest : EventGateTest() {

    @Test
    fun `drop game`() = runTest {
        val user = requireParticipant("shizov")
        val moveDiceValue = 6
        val dropDiceValue = 4
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, moveDiceValue))
        eventGate.parseAndHandleSuspend("${user.name}:${Action.Key.GameRoll}")
        eventGate.eventManager.suspendConsumeAction(GameDrop(user, dropDiceValue))
        assertTrue(inventoryOf(user).isEmpty())
        assertEquals(Game.Status.Dropped, lastGameOf(user)?.status)
        assertEquals(expected = moveDiceValue - dropDiceValue, positionOf(user))
    }

    @Test
    fun `drop game with SamuraiLunge`() = runTest {
        val user = requireParticipant("solll")
        val moveDiceValue = 5
        val dropDiceValue = 4
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, moveDiceValue))
        eventGate.parseAndHandleSuspend("${user.name}:${Action.Key.GameRoll}")
        val item = SamuraiLunge.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        eventGate.eventManager.suspendConsumeAction(GameDrop(user, dropDiceValue))
        assertTrue(inventoryOf(user).isEmpty())
        assertTrue(effectsOf(user).none { it is DropReverse })
        assertEquals(Game.Status.Dropped, lastGameOf(user)?.status)
        assertEquals(expected = moveDiceValue + dropDiceValue, positionOf(user))
    }
}
