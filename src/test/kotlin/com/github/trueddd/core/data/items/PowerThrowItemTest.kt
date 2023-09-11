package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.items.PowerThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PowerThrowItemTest : EventGateTest() {

    @Test
    fun `power throw on 3`() = runTest {
        val user = requireParticipant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 3))
        assertEquals(4, eventGate.stateHolder.current.players[user]!!.position)
    }

    @Test
    fun `power throw on 6`() = runTest {
        val user = requireParticipant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 6))
        assertEquals(7, eventGate.stateHolder.current.players[user]!!.position)
    }

    @Test
    fun `power throw overflow`() = runTest {
        val user = requireParticipant("solll")
        repeat(5) {
            eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create(chargesLeft = 1)))
        }
        println(eventGate.stateHolder.current[user.name]!!.effects.toString())
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 6))
        println(eventGate.stateHolder.current[user.name]!!.effects.toString())
        assertEquals(10, eventGate.stateHolder.current.players[user]!!.position)
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.effects.size)
    }

    @Test
    fun `power throw removal after move`() = runTest {
        val user = requireParticipant("solll")
        repeat(5) {
            eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        }
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        assertEquals(10, eventGate.stateHolder.current.players[user]!!.position)
        assertEquals(0, eventGate.stateHolder.current.players[user]!!.effects.size)
    }

    @Test
    fun `power throw with charges`() = runTest {
        val user = requireParticipant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create(chargesLeft = 2)))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 6))
        assertEquals(7, eventGate.stateHolder.current.players[user]!!.position)
        assertEquals(
            expected = 1,
            eventGate.stateHolder.current.players[user]!!
                .effects.filterIsInstance<PowerThrow>()
                .firstOrNull()
                ?.chargesLeft
        )
    }
}
