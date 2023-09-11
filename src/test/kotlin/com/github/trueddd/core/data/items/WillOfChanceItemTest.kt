package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.data.items.DiceRollModifier
import com.github.trueddd.data.items.WillOfChance
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WillOfChanceItemTest : EventGateTest() {

    @Test
    fun `receive on odd`() = runTest {
        val user = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 3))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WillOfChance.create()))
        assertEquals(expected = -2, effectsOf(user).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 3))
        assertEquals(expected = 4, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `receive on even`() = runTest {
        val user = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 4))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WillOfChance.create()))
        assertEquals(expected = 2, effectsOf(user).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 3))
        assertEquals(expected = 9, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }
}
