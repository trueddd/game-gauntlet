package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.GreatEvent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GreatEventItemTest : EventGateTest() {

    @Test
    fun `online 2`() = runTest {
        val user = getRandomPlayerName()
        val item = GreatEvent.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("2")))
        assertEquals(expected = 2, stateOf(user).modifiersSum)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `online 3`() = runTest {
        val user = getRandomPlayerName()
        val item = GreatEvent.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("3")))
        assertEquals(expected = 3, stateOf(user).modifiersSum)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `fail to parse`() = runTest {
        val user = getRandomPlayerName()
        val item = GreatEvent.create()
        handleAction(ItemReceive(user, item))
        assertFailsWith<IllegalArgumentException> { handleAction(ItemUse(user, item.uid, listOf("-1"))) }
    }
}
