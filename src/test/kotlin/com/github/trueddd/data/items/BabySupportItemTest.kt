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

class BabySupportItemTest : EventGateTest() {

    @Test
    fun `receive by leader`() = runTest {
        val leader = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(leader, 6))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(leader, BabySupport.create()))
        assertTrue(effectsOf(leader).isEmpty())
    }

    @Test
    fun `regular use`() = runTest {
        val outsider = requireParticipant("solll")
        val leader = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(leader, 6))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(outsider, BabySupport.create()))
        assertEquals(expected = 2, effectsOf(outsider).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        eventGate.eventManager.suspendConsumeAction(BoardMove(outsider, 2))
        eventGate.eventManager.suspendConsumeAction(GameRoll(outsider, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(outsider, Game.Status.Finished))
        assertEquals(expected = 2, effectsOf(outsider).filterIsInstance<DiceRollModifier>().sumOf { it.modifier })
        assertEquals(expected = 1, effectsOf(outsider).filterIsInstance<BabySupport>().first().chargesLeft)
        eventGate.eventManager.suspendConsumeAction(BoardMove(outsider, 2))
        eventGate.eventManager.suspendConsumeAction(GameRoll(outsider, Game.Id(2)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(outsider, Game.Status.Finished))
        assertEquals(expected = 8, positionOf(outsider))
        assertTrue(effectsOf(outsider).isEmpty())
    }
}
