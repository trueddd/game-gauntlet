package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.data.Game
import com.github.trueddd.items.ForgotMyGame
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ForgotMyGameItemTest : EventGateTest() {

    @Test
    fun `get item`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, ForgotMyGame.create()))
        assertEquals(expected = 1, pendingEventsOf(user).size)
    }

    @Test
    fun `result - reroll`() = runTest {
        val user = requireRandomParticipant()
        val item = ForgotMyGame.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("1")))
        assertEquals(expected = 0, pendingEventsOf(user).size)
        assertEquals(expected = 1, stateOf(user).gameHistory.count { it.status == Game.Status.Rerolled })
        assertEquals(expected = 0, stateOf(user).gameHistory.count { it.status == Game.Status.InProgress })
        assertFalse(stateOf(user).boardMoveAvailable)
    }

    @Test
    fun `result - cancel`() = runTest {
        val user = requireRandomParticipant()
        val item = ForgotMyGame.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item.uid, listOf("0")))
        assertEquals(expected = 0, pendingEventsOf(user).size)
        assertEquals(expected = 0, stateOf(user).gameHistory.count { it.status == Game.Status.Rerolled })
        assertEquals(expected = 1, stateOf(user).gameHistory.count { it.status == Game.Status.InProgress })
        assertFalse(stateOf(user).boardMoveAvailable)
    }
}
