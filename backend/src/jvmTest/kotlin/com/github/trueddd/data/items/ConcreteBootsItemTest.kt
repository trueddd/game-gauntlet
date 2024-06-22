package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.ConcreteBoots
import com.github.trueddd.items.Earthquake
import com.github.trueddd.items.PowerThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConcreteBootsItemTest : EventGateTest() {

    @Test
    fun `limit movement - move on 7`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 3, positionOf(user))
    }

    @Test
    fun `limit movement - move on 6`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 3, positionOf(user))
    }

    @Test
    fun `limit movement - move on 10`() = runTest {
        val user = getRandomPlayerName()
        repeat(4) {
            handleAction(ItemReceive(user, PowerThrow.create()))
        }
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 5, positionOf(user))
    }

    @Test
    fun `limit movement - move on 10 with additional modifiers`() = runTest {
        val user = getRandomPlayerName()
        repeat(6) {
            handleAction(ItemReceive(user, PowerThrow.create()))
        }
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 5, positionOf(user))
    }

    @Test
    fun `stay on place on earthquake`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 3, positionOf(user))
        handleAction(ItemReceive(user, Earthquake.create()))
        assertEquals(expected = 3, positionOf(user))
    }

    @Test
    fun `stay on place after nuke`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 3, positionOf(user))
        handleAction(GlobalEvent(GlobalEvent.Type.Nuke, stageIndex = 0, epicenterStintIndex = 0))
        assertEquals(expected = 3, positionOf(user))
    }

    @Test
    fun `stay on place after tornado`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 3, positionOf(user))
        handleAction(GlobalEvent(GlobalEvent.Type.Tornado, stageIndex = 0, epicenterStintIndex = 1))
        assertEquals(expected = 3, positionOf(user))
    }

    @Test
    fun `drop boots after 3 moves`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, ConcreteBoots.create()))
        val diceValue = 6
        repeat(ConcreteBoots.MOVES_COUNT) {
            handleAction(BoardMove(user, diceValue))
            handleAction(GameRoll(user, Game.Id(it)))
            handleAction(GameStatusChange(user, Game.Status.Finished))
        }
        assertTrue(effectsOf(user).isEmpty())
    }
}
