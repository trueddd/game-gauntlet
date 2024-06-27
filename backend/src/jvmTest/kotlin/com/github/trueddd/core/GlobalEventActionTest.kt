package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.DiceRollModifier
import com.github.trueddd.items.PowerThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalEventActionTest : EventGateTest() {

    @Test
    fun `nuke - stage 1`() = runTest {
        val (user1, user2, user3, user4) = getPlayerNames()
        // user 1 moves
        handleAction(BoardMove(user1, diceValue = 6))
        handleAction(GameRoll(user1, Game.Id(1)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        handleAction(BoardMove(user1, diceValue = 5))
        handleAction(GameRoll(user1, Game.Id(3)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        // user 2 moves
        handleAction(BoardMove(user2, diceValue = 6))
        handleAction(GameRoll(user2, Game.Id(3)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        handleAction(BoardMove(user2, diceValue = 4))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        // user 3 moves
        handleAction(ItemReceive(user3, PowerThrow.create()))
        handleAction(BoardMove(user3, diceValue = 6))
        handleAction(GameRoll(user3, Game.Id(4)))
        // user 4 moves
        handleAction(BoardMove(user4, diceValue = 6))
        handleAction(GameRoll(user4, Game.Id(0)))
        // checks
        assertEquals(expected = 11, positionOf(user1))
        assertEquals(expected = 10, positionOf(user2))
        assertEquals(expected = 7, positionOf(user3))
        assertEquals(expected = 6, positionOf(user4))
        handleAction(GlobalEvent(GlobalEvent.Type.Nuke, stageIndex = 0, epicenterStintIndex = 1))
        assertEquals(expected = 17, positionOf(user1))
        assertEquals(expected = 4, positionOf(user2))
        assertEquals(expected = 1, positionOf(user3))
        assertEquals(expected = 3, positionOf(user4))
        assertEquals(expected = -3, effectsOf(user1).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = -3, effectsOf(user2).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = -3, effectsOf(user3).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = -1, effectsOf(user4).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
    }

    @Test
    fun `tornado - stage 1`() = runTest {
        val (user1, user2, user3, user4) = getPlayerNames()
        // user 1 moves
        handleAction(BoardMove(user1, diceValue = 6))
        handleAction(GameRoll(user1, Game.Id(1)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        handleAction(BoardMove(user1, diceValue = 5))
        handleAction(GameRoll(user1, Game.Id(3)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        // user 2 moves
        handleAction(BoardMove(user2, diceValue = 6))
        handleAction(GameRoll(user2, Game.Id(3)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        handleAction(BoardMove(user2, diceValue = 4))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        // user 3 moves
        handleAction(ItemReceive(user3, PowerThrow.create()))
        handleAction(BoardMove(user3, diceValue = 6))
        handleAction(GameRoll(user3, Game.Id(4)))
        // user 4 moves
        handleAction(BoardMove(user4, diceValue = 6))
        handleAction(GameRoll(user4, Game.Id(0)))
        // checks
        assertEquals(expected = 11, positionOf(user1))
        assertEquals(expected = 10, positionOf(user2))
        assertEquals(expected = 7, positionOf(user3))
        assertEquals(expected = 6, positionOf(user4))
        handleAction(GlobalEvent(GlobalEvent.Type.Tornado, stageIndex = 0, epicenterStintIndex = 1))
        assertEquals(expected = 10, positionOf(user1))
        assertEquals(expected = 11, positionOf(user2))
        assertEquals(expected = 14, positionOf(user3))
        assertEquals(expected = 9, positionOf(user4))
        assertEquals(expected = -3, effectsOf(user1).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = -3, effectsOf(user2).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = -3, effectsOf(user3).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = -1, effectsOf(user4).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
    }

    @Test
    fun `most populated stint - 1`() = runTest {
        val (user1, user2, user3, user4) = getPlayerNames()
        // user 1 moves
        handleAction(BoardMove(user1, diceValue = 6))
        handleAction(GameRoll(user1, Game.Id(1)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        handleAction(BoardMove(user1, diceValue = 5))
        handleAction(GameRoll(user1, Game.Id(3)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        // user 2 moves
        handleAction(BoardMove(user2, diceValue = 6))
        handleAction(GameRoll(user2, Game.Id(3)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        handleAction(BoardMove(user2, diceValue = 6))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        handleAction(BoardMove(user2, diceValue = 3))
        handleAction(GameRoll(user2, Game.Id(0)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        // user 3 moves
        handleAction(ItemReceive(user3, PowerThrow.create()))
        handleAction(BoardMove(user3, diceValue = 6))
        handleAction(GameRoll(user3, Game.Id(4)))
        // user 4 moves
        handleAction(BoardMove(user4, diceValue = 6))
        handleAction(GameRoll(user4, Game.Id(0)))
        // checks
        assertEquals(expected = 1, stintOf(user1))
        assertEquals(expected = 2, stintOf(user2))
        assertEquals(expected = 0, stintOf(user3))
        assertEquals(expected = 0, stintOf(user4))
        assertEquals(expected = 0, eventGate.stateHolder.current.getMostPopulatedStintIndex())
    }

    @Test
    fun `most populated stint - 2`() = runTest {
        val (user1, user2, user3, user4) = getPlayerNames()
        // user 1 moves
        handleAction(BoardMove(user1, diceValue = 6))
        handleAction(GameRoll(user1, Game.Id(1)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        handleAction(BoardMove(user1, diceValue = 5))
        handleAction(GameRoll(user1, Game.Id(3)))
        handleAction(GameStatusChange(user1, Game.Status.Finished))
        // user 2 moves
        handleAction(BoardMove(user2, diceValue = 6))
        handleAction(GameRoll(user2, Game.Id(3)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        handleAction(BoardMove(user2, diceValue = 6))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        // user 3 moves
        handleAction(BoardMove(user3, diceValue = 6))
        handleAction(GameRoll(user3, Game.Id(4)))
        handleAction(GameStatusChange(user3, Game.Status.Finished))
        handleAction(BoardMove(user3, diceValue = 2))
        handleAction(GameRoll(user3, Game.Id(2)))
        handleAction(GameStatusChange(user3, Game.Status.Finished))
        // user 4 moves
        handleAction(BoardMove(user4, diceValue = 6))
        handleAction(GameRoll(user4, Game.Id(0)))
        handleAction(GameStatusChange(user4, Game.Status.Finished))
        handleAction(BoardMove(user4, diceValue = 2))
        handleAction(GameRoll(user4, Game.Id(2)))
        handleAction(GameStatusChange(user4, Game.Status.Finished))
        // checks
        assertEquals(expected = 1, stintOf(user1))
        assertEquals(expected = 1, stintOf(user2))
        assertEquals(expected = 1, stintOf(user3))
        assertEquals(expected = 1, stintOf(user4))
        assertEquals(expected = 1, eventGate.stateHolder.current.getMostPopulatedStintIndex())
    }
}
