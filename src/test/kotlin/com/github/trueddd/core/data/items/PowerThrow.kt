package com.github.trueddd.core.data.items

import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.PowerThrow
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PowerThrow {

    private val eventGate = provideEventGate()

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
