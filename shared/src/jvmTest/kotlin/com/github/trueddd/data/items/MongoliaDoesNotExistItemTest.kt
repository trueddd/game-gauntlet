package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MongoliaDoesNotExistItemTest : EventGateTest() {

    @Test
    fun `successful use`() = runTest {
        val user = requireRandomParticipant()
        val item = MongoliaDoesNotExist.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf("1")))
        assertEquals(expected = 2, stateOf(user).modifiersSum)
        assertEquals(expected = 1, effectsOf(user).size)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `unsuccessful use`() = runTest {
        val user = requireRandomParticipant()
        val item = MongoliaDoesNotExist.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf("0")))
        assertEquals(expected = -2, stateOf(user).modifiersSum)
        assertEquals(expected = 1, effectsOf(user).size)
        assertEquals(expected = 0, pendingEventsOf(user).size)
    }

    @Test
    fun `parsing failure`() = runTest {
        val user = requireRandomParticipant()
        val item = MongoliaDoesNotExist.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, stateOf(user).modifiersSum)
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 1, pendingEventsOf(user).size)
    }
}
