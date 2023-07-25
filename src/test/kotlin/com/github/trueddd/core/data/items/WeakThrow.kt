package com.github.trueddd.core.data.items

import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.WeakThrow
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class WeakThrow {

    private val eventGate = provideEventGate()

    @Test
    fun `weak throw on 5`() = runBlocking {
        val user = Participant("keli")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        assertEquals(4, eventGate.stateHolder.current.players[user]!!.position)
    }

    @Test
    fun `weak throw on 1`() = runBlocking {
        val user = Participant("keli")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 1))
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.position)
    }
}
