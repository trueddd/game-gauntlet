package com.github.trueddd.core

import com.github.trueddd.core.events.BoardMove
import com.github.trueddd.core.events.GameDrop
import com.github.trueddd.core.events.ItemReceive
import com.github.trueddd.core.events.ItemUse
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.SamuraiLunge
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SamuraiLungeItemTest {

    private val eventGate = provideEventGate()

    @Test
    fun `test item`() = runBlocking {
        val user = Participant("solll")
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 5))
        val item = SamuraiLunge.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        eventGate.eventManager.suspendConsumeAction(GameDrop(user, 4))
        assertTrue(eventGate.stateHolder.current.players[user]!!.inventory.isEmpty())
        assertFalse(eventGate.stateHolder.current.players[user]!!.dropPenaltyReversed)
        assertEquals(9, eventGate.stateHolder.current.players[user]!!.position)
    }
}
