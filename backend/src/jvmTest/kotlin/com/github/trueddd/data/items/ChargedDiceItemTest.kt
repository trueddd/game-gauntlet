package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.ChargedDice
import com.github.trueddd.items.PowerThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChargedDiceItemTest : EventGateTest() {

    @Test
    fun `make move with no modifiers`() = runTest {
        val user = getRandomPlayerName()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(ItemReceive(user, ChargedDice.create()))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(BoardMove(user, diceValue = 4))
        assertEquals(expected = 1, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `make move with modifiers`() = runTest {
        val user = getRandomPlayerName()
        handleAction(BoardMove(user, diceValue = 6))
        handleAction(ItemReceive(user, ChargedDice.create()))
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(BoardMove(user, diceValue = 4))
        assertEquals(expected = 1, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }
}
