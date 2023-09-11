package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.data.items.BananaSkin
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BananaSkinItemTest : EventGateTest() {

    @Test
    fun `item use`() = runTest {
        val user = requireParticipant("shizov")
        val item = BananaSkin.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).count { it is BananaSkin })
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 4))
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, effectsOf(user).size)
    }

    @Test
    fun `step on banana`() = runTest {
        val userToPlace = requireParticipant("shizov")
        val userToStep = requireParticipant("solll")
        val item = BananaSkin.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(userToPlace, item))
        eventGate.eventManager.suspendConsumeAction(BoardMove(userToPlace, 4))
        eventGate.eventManager.suspendConsumeAction(GameRoll(userToPlace, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(userToPlace, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(ItemUse(userToPlace, item.uid))

        eventGate.eventManager.suspendConsumeAction(BoardMove(userToStep, 4))
        assertEquals(expected = 2, positionOf(userToStep))
        assertTrue(eventGate.stateHolder.current.boardTraps.isEmpty())
    }
}
