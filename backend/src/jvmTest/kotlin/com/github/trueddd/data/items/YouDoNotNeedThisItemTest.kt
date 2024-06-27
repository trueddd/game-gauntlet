package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.PowerThrow
import com.github.trueddd.items.WeakThrow
import com.github.trueddd.items.WheelItem
import com.github.trueddd.items.YouDoNotNeedThis
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YouDoNotNeedThisItemTest : EventGateTest() {

    @Test
    fun `drop buff`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(ItemReceive(user, WeakThrow.create()))
        handleAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(expected = 1, effectsOf(user).size)
        assertTrue(effectsOf(user).none { it is WheelItem.Effect.Buff })
    }

    @Test
    fun `non-drop buff`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, WeakThrow.create()))
        handleAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(expected = 1, effectsOf(user).size)
        assertTrue(effectsOf(user).none { it is WheelItem.Effect.Buff })
    }

    @Test
    fun `drop single buff`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(ItemReceive(user, WeakThrow.create()))
        handleAction(ItemReceive(user, YouDoNotNeedThis.create()))
        assertEquals(expected = 2, effectsOf(user).size)
        assertTrue(effectsOf(user).any { it is WheelItem.Effect.Buff })
        assertTrue(effectsOf(user).any { it is WheelItem.Effect.Debuff })
    }
}
