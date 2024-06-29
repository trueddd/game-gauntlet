package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.EasterCakeBang
import com.github.trueddd.items.HoleyPockets
import com.github.trueddd.items.LoyalModerator
import com.github.trueddd.items.RatMove
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EasterCakeBangItemTest : EventGateTest() {

    @Test
    fun `drop attempt`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, EasterCakeBang.create()))
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameDrop(user, diceValue = 4))
        assertEquals(expected = 4, positionOf(user))
        assertEquals(Game.Status.InProgress, lastGameOf(user)?.status)
    }

    @Test
    fun `remove effect after game complete`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, EasterCakeBang.create()))
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `effect removal attempt - holey pockets`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, EasterCakeBang.create()))
        handleAction(ItemReceive(user, HoleyPockets.create()))
        assertIs<EasterCakeBang>(effectsOf(user).first())
    }

    @Test
    fun `effect removal attempt - loyal moderator`() = runTest {
        val user = getRandomPlayerName()
        val bangItem = EasterCakeBang.create()
        val moderatorItem = LoyalModerator.create()
        handleAction(ItemReceive(user, bangItem))
        handleAction(ItemReceive(user, moderatorItem))
        assertFailsWith<IllegalArgumentException> {
            handleAction(ItemUse(user, moderatorItem.uid, listOf(bangItem.uid)))
        }
    }

    @Test
    fun `effect removal attempt - rat move`() = runTest {
        val (user1, user2) = getPlayerNames()
        handleAction(ItemReceive(user1, EasterCakeBang.create()))
        val item = RatMove.create()
        handleAction(ItemReceive(user2, item))
        handleAction(ItemUse(user2, item.uid, listOf(user1)))
        assertIs<EasterCakeBang>(effectsOf(user1).first())
        assertTrue(pendingEventsOf(user2).isEmpty())
    }
}
