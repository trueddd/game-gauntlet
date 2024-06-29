package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.AwfulEvent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AwfulEventItemTest : EventGateTest() {

    @Test
    fun `online 2`() = runTest {
        val user = getRandomPlayerName()
        val item = AwfulEvent.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("2")))
        println(stateOf(user).toString())
        assertEquals(expected = -2, stateOf(user).modifiersSum)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `online 3`() = runTest {
        val user = getRandomPlayerName()
        val item = AwfulEvent.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("3")))
        assertEquals(expected = -3, stateOf(user).modifiersSum)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `fail to parse`() = runTest {
        val user = getRandomPlayerName()
        val item = AwfulEvent.create()
        handleAction(ItemReceive(user, item))
        assertFailsWith<IllegalArgumentException> { handleAction(ItemUse(user, item.uid, listOf("-1"))) }
    }
}
