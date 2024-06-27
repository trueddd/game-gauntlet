package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.Shitter
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals

class ShitterItemTest : EventGateTest() {

    @Test
    fun `got by leader`() = runTest {
        val (user1, user2, user3) = getPlayerNames()
        handleAction(BoardMove(user1, diceValue = 1))
        handleAction(BoardMove(user2, diceValue = 2))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(ItemReceive(user3, Shitter.create()))
        assertEquals(expected = -3, stateOf(user3).modifiersSum)
    }

    @Test
    fun `got by 2nd placer`() = runTest {
        val (user1, user2, user3) = getPlayerNames()
        handleAction(BoardMove(user1, diceValue = 1))
        handleAction(BoardMove(user2, diceValue = 2))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(ItemReceive(user2, Shitter.create()))
        assertEquals(expected = -2, stateOf(user2).modifiersSum)
    }

    @Test
    fun `got by 3rd placer`() = runTest {
        val (user1, user2, user3) = getPlayerNames()
        handleAction(BoardMove(user1, diceValue = 1))
        handleAction(BoardMove(user2, diceValue = 2))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(ItemReceive(user1, Shitter.create()))
        assertEquals(expected = -1, stateOf(user1).modifiersSum)
    }

    @Test
    fun `got by 4th placer`() = runTest {
        val (user1, user2, user3, user4) = getPlayerNames()
        handleAction(BoardMove(user1, diceValue = 1))
        handleAction(BoardMove(user2, diceValue = 2))
        handleAction(BoardMove(user3, diceValue = 3))
        handleAction(BoardMove(user4, diceValue = 4))
        handleAction(ItemReceive(user1, Shitter.create()))
        assertEquals(expected = 0, stateOf(user1).modifiersSum)
    }

    @Test
    fun `got when all equalized`() = runTest {
        val user = getRandomPlayerName()
        handleAction(ItemReceive(user, Shitter.create()))
        assertEquals(expected = 0, stateOf(user).modifiersSum)
    }
}
