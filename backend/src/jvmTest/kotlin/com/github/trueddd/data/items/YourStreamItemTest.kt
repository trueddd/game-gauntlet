package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.YourStream
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YourStreamItemTest : EventGateTest() {

    @Test
    fun `use 1`() = runTest {
        val user = getRandomPlayerName()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(ItemReceive(user, YourStream.create()))
        assertEquals(expected = 1, effectsOf(user).size)
        handleAction(GameRoll(user, Game.Id(4)))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `use 2`() = runTest {
        val user = getRandomPlayerName()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(GameRoll(user, Game.Id(1)))
        assertTrue(effectsOf(user).isEmpty())
        handleAction(ItemReceive(user, YourStream.create()))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = 1, effectsOf(user).size)
        handleAction(BoardMove(user, diceValue = 2))
        handleAction(GameRoll(user, Game.Id(4)))
        assertTrue(effectsOf(user).isEmpty())
    }
}
