package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnbelievableDemocracyItemTest : EventGateTest() {

    @Test
    fun `get buff after poll`() = runTest {
        val user = requireRandomParticipant()
        val item = UnbelievableDemocracy.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("1")))
        assertTrue(pendingEventsOf(user).isEmpty())
        requireParticipants().forEach { player ->
            assertEquals(expected = 1, stateOf(player).modifiersSum)
        }
    }

    @Test
    fun `get debuff after poll`() = runTest {
        val user = requireRandomParticipant()
        val item = UnbelievableDemocracy.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("0")))
        assertTrue(pendingEventsOf(user).isEmpty())
        requireParticipants().forEach { player ->
            assertEquals(expected = -1, stateOf(player).modifiersSum)
        }
    }

    @Test
    fun `validation fail - 1`() = runTest {
        val user = requireRandomParticipant()
        val item = UnbelievableDemocracy.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid))
        assertTrue(pendingEventsOf(user).isNotEmpty())
        requireParticipants().forEach { player ->
            assertEquals(expected = 0, stateOf(player).modifiersSum)
        }
    }

    @Test
    fun `validation fail - 2`() = runTest {
        val user = requireRandomParticipant()
        val item = UnbelievableDemocracy.create()
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("q")))
        assertTrue(pendingEventsOf(user).isNotEmpty())
        requireParticipants().forEach { player ->
            assertEquals(expected = 0, stateOf(player).modifiersSum)
        }
    }
}
