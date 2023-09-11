package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.items.WeakThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WeakThrowItemTest : EventGateTest() {

    @Test
    fun `weak throw on 5`() = runTest {
        val user = requireParticipant("keli")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        assertEquals(4, eventGate.stateHolder.current.players[user]!!.position)
    }

    @Test
    fun `weak throw on 1`() = runTest {
        val user = requireParticipant("keli")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 1))
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.position)
    }
}
