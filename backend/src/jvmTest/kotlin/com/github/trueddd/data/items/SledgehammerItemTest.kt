package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.ConcreteBoots
import com.github.trueddd.items.Sledgehammer
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SledgehammerItemTest : EventGateTest() {

    @Test
    fun `use on player without boots`() = runTest {
        val (user1, user2) = getPlayerNames()
        val sledgehammer = Sledgehammer.create()
        handleAction(ItemReceive(user1, sledgehammer))
        val state = eventGate.stateHolder.current
        handleAction(ItemUse(user1, sledgehammer.uid, listOf(user2, "1")))
        assertEquals(expected = state, actual = eventGate.stateHolder.current)
    }

    @Test
    fun `use carefully`() = runTest {
        val (user1, user2) = getPlayerNames()
        val sledgehammer = Sledgehammer.create()
        handleAction(ItemReceive(user1, sledgehammer))
        assertEquals(expected = 1, inventoryOf(user1).size)
        handleAction(ItemReceive(user2, ConcreteBoots.create()))
        assertEquals(expected = 1, effectsOf(user2).size)
        handleAction(ItemUse(user1, sledgehammer.uid, listOf(user2, "1")))
        assertEquals(expected = 0, effectsOf(user2).size)
        assertEquals(expected = 0, inventoryOf(user1).size)
    }

    @Test
    fun `use carelessly`() = runTest {
        val (user1, user2) = getPlayerNames()
        val sledgehammer = Sledgehammer.create()
        handleAction(ItemReceive(user1, sledgehammer))
        assertEquals(expected = 1, inventoryOf(user1).size)
        handleAction(ItemReceive(user2, ConcreteBoots.create()))
        assertEquals(expected = 1, effectsOf(user2).size)
        handleAction(ItemUse(user1, sledgehammer.uid, listOf(user2, "0")))
        assertEquals(expected = 1, effectsOf(user2).size)
        assertEquals(expected = -3, stateOf(user2).modifiersSum)
        assertEquals(expected = 0, inventoryOf(user1).size)
    }

    @Test
    fun `use carelessly and move`() = runTest {
        val (user1, user2) = getPlayerNames()
        val sledgehammer = Sledgehammer.create()
        handleAction(ItemReceive(user1, sledgehammer))
        handleAction(ItemReceive(user2, ConcreteBoots.create()))
        handleAction(ItemUse(user1, sledgehammer.uid, listOf(user2, "0")))
        assertEquals(expected = 1, effectsOf(user2).size)
        val diceValue = 6
        handleAction(BoardMove(user2, diceValue))
        assertEquals(expected = diceValue + Sledgehammer.Debuff.MODIFIER, positionOf(user2))
        assertEquals(expected = 0, effectsOf(user2).size)
    }
}
