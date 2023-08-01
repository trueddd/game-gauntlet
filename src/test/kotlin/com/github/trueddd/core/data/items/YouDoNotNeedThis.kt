package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.items.PowerThrow
import com.github.trueddd.data.items.WeakThrow
import com.github.trueddd.data.items.WheelItem
import com.github.trueddd.data.items.YouDoNotNeedThis
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YouDoNotNeedThis : EventGateTest() {

    @Test
    fun `drop buff`() = runTest {
        val user = requireParticipant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.effects.size)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.none { it is WheelItem.Effect.Buff })
    }

    @Test
    fun `non-drop buff`() = runTest {
        val user = requireParticipant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(1, eventGate.stateHolder.current.players[user]!!.effects.size)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.none { it is WheelItem.Effect.Buff })
    }

    @Test
    fun `drop single buff`() = runTest {
        val user = requireParticipant("solll")
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, PowerThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, WeakThrow.create()))
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(2, eventGate.stateHolder.current.players[user]!!.effects.size)
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.any { it is WheelItem.Effect.Buff })
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.any { it is WheelItem.Effect.Debuff })
    }
}
