package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.SamuraiLunge
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DropGameActionTest : EventGateTest() {

    @Test
    fun `drop game`() = runTest {
        val user = getRandomPlayerName()
        val moveDiceValue = 6
        val dropDiceValue = 4
        handleAction(BoardMove(user, moveDiceValue))
        eventGate.parseAndHandle("$user:${Action.Key.GameRoll}")
        handleAction(GameDrop(user, dropDiceValue))
        assertTrue(inventoryOf(user).isEmpty())
        assertEquals(Game.Status.Dropped, lastGameOf(user)?.status)
        assertEquals(expected = moveDiceValue - dropDiceValue, positionOf(user))
    }

    @RepeatedTest(10)
    fun `drop game - send rolled dice`() = runTest {
        val user = getRandomPlayerName()
        val moveDiceValue = 6
        val dropDiceValue = 4
        handleAction(BoardMove(user, moveDiceValue))
        eventGate.parseAndHandle("$user:${Action.Key.GameRoll}")
        eventGate.parseAndHandle("$user:${Action.Key.GameDrop}:$dropDiceValue")
        assertEquals(Game.Status.Dropped, lastGameOf(user)?.status)
        assertEquals(expected = moveDiceValue - dropDiceValue, positionOf(user))
    }

    @Test
    fun `drop game with SamuraiLunge`() = runTest {
        val user = getRandomPlayerName()
        val moveDiceValue = 5
        val dropDiceValue = 4
        handleAction(BoardMove(user, moveDiceValue))
        eventGate.parseAndHandle("$user:${Action.Key.GameRoll}")
        val item = SamuraiLunge.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid))
        handleAction(GameDrop(user, dropDiceValue))
        assertTrue(inventoryOf(user).isEmpty())
        assertTrue(effectsOf(user).none { it is SamuraiLunge.Buff })
        assertEquals(Game.Status.Dropped, lastGameOf(user)?.status)
        assertEquals(expected = moveDiceValue + dropDiceValue, positionOf(user))
    }
}
