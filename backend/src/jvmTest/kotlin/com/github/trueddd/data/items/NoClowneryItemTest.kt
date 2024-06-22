package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.HoleyPockets
import com.github.trueddd.items.LoyalModerator
import com.github.trueddd.items.NoClownery
import com.github.trueddd.items.RatMove
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NoClowneryItemTest : EventGateTest() {

    @Test
    fun `item dispose - after special spot`() = runTest {
        val user = getRandomPlayerName()
        val item = NoClownery.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        handleAction(BoardMove(user, diceValue = 4))
        assertEquals(expected = 0, effectsOf(user).size)
    }

    @RepeatedTest(10)
    fun `attempt to roll while debuffed`() = runTest {
        val user = getRandomPlayerName()
        val item = NoClownery.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        eventGate.parseAndHandle("$user:${Action.Key.ItemReceive}")
        assertEquals(expected = 1, effectsOf(user).size)
        assertEquals(expected = 0, pendingEventsOf(user).size)
        assertEquals(expected = 0, inventoryOf(user).size)
    }

    @Test
    fun `inventory flush - rat move`() = runTest {
        val (user1, user2) = getPlayerNames()
        val noClownery = NoClownery.create()
        val ratMove = RatMove.create()
        handleAction(ItemReceive(user1, noClownery))
        handleAction(BoardMove(user1, diceValue = 4))
        handleAction(ItemReceive(user2, ratMove))
        assertEquals(expected = 1, effectsOf(user1).size)
        handleAction(ItemUse(user2, ratMove.uid, user1))
        assertEquals(expected = 1, effectsOf(user1).size)
    }

    @Test
    fun `inventory flush - holey pockets`() = runTest {
        val (user1) = getPlayerNames()
        val noClownery = NoClownery.create()
        val holeyPockets = HoleyPockets.create()
        handleAction(ItemReceive(user1, noClownery))
        handleAction(BoardMove(user1, diceValue = 4))
        handleAction(ItemReceive(user1, holeyPockets))
        assertIs<NoClownery>(effectsOf(user1).first())
    }

    @Test
    fun `inventory flush - loyal moderator`() = runTest {
        val (user1) = getPlayerNames()
        val noClownery = NoClownery.create()
        val loyalModerator = LoyalModerator.create()
        handleAction(ItemReceive(user1, noClownery))
        handleAction(BoardMove(user1, diceValue = 3))
        handleAction(ItemReceive(user1, loyalModerator))
        handleAction(ItemUse(user1, loyalModerator.uid, noClownery.uid))
        assertIs<NoClownery>(effectsOf(user1).first())
    }
}
