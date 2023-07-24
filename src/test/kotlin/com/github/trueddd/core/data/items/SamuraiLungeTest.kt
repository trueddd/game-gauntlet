package com.github.trueddd.core.data.items

import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameDrop
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.DropReverse
import com.github.trueddd.data.items.SamuraiLunge
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SamuraiLungeTest {

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
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.none { it is DropReverse })
        assertEquals(9, eventGate.stateHolder.current.players[user]!!.position)
    }
}
