package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.ClimbingRope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ClimbingRopeItemTest : EventGateTest() {

    @Test
    fun `drop game with item`() = runTest {
        val user = requireRandomParticipant()
        val item = ClimbingRope.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 1, effectsOf(user).size)
        handleAction(GameDrop(user, diceValue = 4))
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 4, positionOf(user))
    }

    @Test
    fun `drop game with no item`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 5))
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameDrop(user, diceValue = 4))
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 1, positionOf(user))
    }

    @Test
    fun `use item and do not drop`() = runTest {
        val user = requireRandomParticipant()
        val item = ClimbingRope.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 1, effectsOf(user).size)
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
