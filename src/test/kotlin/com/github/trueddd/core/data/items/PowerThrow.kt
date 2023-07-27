package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.PowerThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PowerThrow : EventGateTest() {

    @Test
    fun `power throw on 3`() = runTest {
        val user = Participant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 3))
        assertEquals(4, eventGate.stateHolder.current.players[user]!!.position)
    }

    @Test
    fun `power throw on 6`() = runTest {
        val user = Participant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 6))
        assertEquals(7, eventGate.stateHolder.current.players[user]!!.position)
    }
}
