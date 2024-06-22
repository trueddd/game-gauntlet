package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.BananaSkin
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BananaSkinItemTest : EventGateTest() {

    @Test
    fun `item use`() = runTest {
        val user = getRandomPlayerName()
        val item = BananaSkin.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).count { it is BananaSkin })
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, effectsOf(user).size)
    }

    @Test
    fun `step on banana`() = runTest {
        val (userToPlace, userToStep) = getPlayerNames()
        val item = BananaSkin.create()
        handleAction(ItemReceive(userToPlace, item))
        handleAction(BoardMove(userToPlace, diceValue = 4))
        handleAction(GameRoll(userToPlace, Game.Id(1)))
        handleAction(GameStatusChange(userToPlace, Game.Status.Finished))
        handleAction(ItemUse(userToPlace, item.uid))

        handleAction(BoardMove(userToStep, diceValue = 4))
        assertEquals(expected = 2, positionOf(userToStep))
        assertTrue(eventGate.stateHolder.current.stateSnapshot.boardTraps.isEmpty())
    }
}
