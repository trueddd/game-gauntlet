package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.*
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EasterCakeBangItemTest : EventGateTest() {

    @Test
    fun `drop attempt`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, EasterCakeBang.create()))
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameDrop(user, diceValue = 4))
        assertEquals(expected = 4, positionOf(user))
        assertEquals(Game.Status.InProgress, lastGameOf(user)?.status)
    }

    @Test
    fun `remove effect after game complete`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, EasterCakeBang.create()))
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `effect removal attempt - holey pockets`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, EasterCakeBang.create()))
        handleAction(ItemReceive(user, HoleyPockets.create()))
        assertIs<EasterCakeBang>(effectsOf(user).first())
    }

    @Test
    fun `effect removal attempt - loyal moderator`() = runTest {
        val user = requireRandomParticipant()
        val bangItem = EasterCakeBang.create()
        val moderatorItem = LoyalModerator.create()
        handleAction(ItemReceive(user, bangItem))
        handleAction(ItemReceive(user, moderatorItem))
        handleAction(ItemUse(user, moderatorItem.uid, listOf(bangItem.uid)))
        assertIs<EasterCakeBang>(effectsOf(user).first())
        assertIs<LoyalModerator>(inventoryOf(user).first())
    }

    @Test
    fun `effect removal attempt - rat move`() = runTest {
        val (user1, user2) = requireParticipants()
        handleAction(ItemReceive(user1, EasterCakeBang.create()))
        val item = RatMove.create()
        handleAction(ItemReceive(user2, item))
        handleAction(ItemUse(user2, item.uid, listOf(user1.name)))
        assertIs<EasterCakeBang>(effectsOf(user1).first())
        assertTrue(pendingEventsOf(user2).isEmpty())
    }
}
