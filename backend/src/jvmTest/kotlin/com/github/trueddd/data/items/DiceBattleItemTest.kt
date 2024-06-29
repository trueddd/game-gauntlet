package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.DiceBattle
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DiceBattleItemTest : EventGateTest() {

    @Test
    fun `dice battle - win`() = runTest {
        val (user1, user2) = getPlayerNames()
        val item = DiceBattle.create()
        handleAction(ItemReceive(user1, item))
        handleAction(ItemUse(user1, item.uid, listOf(user2, "4", "2")))
        assertEquals(expected = 4, stateOf(user1).modifiersSum)
        assertEquals(expected = -2, stateOf(user2).modifiersSum)
        assertTrue(pendingEventsOf(user1).isEmpty())
    }

    @Test
    fun `dice battle - lose`() = runTest {
        val (user1, user2) = getPlayerNames()
        val item = DiceBattle.create()
        handleAction(ItemReceive(user1, item))
        handleAction(ItemUse(user1, item.uid, listOf(user2, "4", "5")))
        assertEquals(expected = -4, stateOf(user1).modifiersSum)
        assertEquals(expected = 5, stateOf(user2).modifiersSum)
        assertTrue(pendingEventsOf(user1).isEmpty())
    }

    @Test
    fun `dice battle - draw`() = runTest {
        val (user1, user2) = getPlayerNames()
        val item = DiceBattle.create()
        handleAction(ItemReceive(user1, item))
        assertFailsWith<IllegalArgumentException> {
            handleAction(ItemUse(user1, item.uid, listOf(user2, "4", "4")))
        }
    }
}
