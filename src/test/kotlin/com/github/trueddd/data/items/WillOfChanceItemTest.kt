package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WillOfChanceItemTest : EventGateTest() {

    @Test
    fun `receive on odd`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(ItemReceive(user, WillOfChance.create()))
        assertEquals(expected = -2, effectsOf(user).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(BoardMove(user, diceValue = 3))
        assertEquals(expected = 4, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `receive on even`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(ItemReceive(user, WillOfChance.create()))
        assertEquals(expected = 2, effectsOf(user).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(BoardMove(user, diceValue = 3))
        assertEquals(expected = 9, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }
}
