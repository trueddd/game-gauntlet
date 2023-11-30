package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.UnrealBoost
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UnrealBoostItemTest : EventGateTest() {

    @Test
    fun `successful use`() = runTest {
        val user = requireRandomParticipant()
        val item = UnrealBoost.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf("1")))
        assertEquals(expected = 0, pendingEventsOf(user).size)
        assertEquals(expected = 3, stateOf(user).modifiersSum)
        assertEquals(expected = 5, effectsOf(user).filterIsInstance<UnrealBoost.Buff>().firstOrNull()?.chargesLeft)
        repeat(5) {
            makeMove(user)
        }
        assertEquals(expected = 0, stateOf(user).modifiersSum)
        assertEquals(expected = null, effectsOf(user).filterIsInstance<UnrealBoost.Buff>().firstOrNull()?.chargesLeft)
    }

    @Test
    fun `unsuccessful use`() = runTest {
        val user = requireRandomParticipant()
        val item = UnrealBoost.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf("0")))
        assertEquals(expected = 0, pendingEventsOf(user).size)
        assertEquals(expected = 0, stateOf(user).modifiersSum)
        assertEquals(expected = null, effectsOf(user).filterIsInstance<UnrealBoost.Buff>().firstOrNull()?.chargesLeft)
    }

    @Test
    fun `parsing failure`() = runTest {
        val user = requireRandomParticipant()
        val item = UnrealBoost.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf("3")))
        assertEquals(expected = 1, pendingEventsOf(user).size)
    }
}
