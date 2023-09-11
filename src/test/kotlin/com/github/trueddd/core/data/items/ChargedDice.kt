package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.data.items.ChargedDice
import com.github.trueddd.data.items.PowerThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChargedDice : EventGateTest() {

    @Test
    fun `make move with no modifiers`() = runTest {
        val user = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, ChargedDice.create()))
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 4))
        assertEquals(expected = 1, eventGate.stateHolder.current.players[user]!!.position)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.isEmpty())
    }

    @Test
    fun `make move with modifiers`() = runTest {
        val user = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 6))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, ChargedDice.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 4))
        assertEquals(expected = 1, eventGate.stateHolder.current.players[user]!!.position)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.isEmpty())
    }
}
