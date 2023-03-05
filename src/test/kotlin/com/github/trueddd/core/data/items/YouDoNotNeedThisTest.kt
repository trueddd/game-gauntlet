package com.github.trueddd.core.data.items

import com.github.trueddd.core.events.ItemReceive
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.WheelItem
import com.github.trueddd.data.items.PowerThrow
import com.github.trueddd.data.items.WeakThrow
import com.github.trueddd.data.items.YouDoNotNeedThis
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class YouDoNotNeedThisTest {

    private val eventGate = provideEventGate()

    @Test
    fun `drop buff`() = runBlocking {
        val user = Participant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.effects.size)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.none { it is WheelItem.Effect.Buff })
    }

    @Test
    fun `non-drop buff`() = runBlocking {
        val user = Participant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.effects.size)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.none { it is WheelItem.Effect.Buff })
    }

    @Test
    fun `drop single buff`() = runBlocking {
        val user = Participant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(2, eventGate.stateHolder.current.players[user]!!.effects.size)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.any { it is WheelItem.Effect.Buff })
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.any { it is WheelItem.Effect.Debuff })
    }
}
