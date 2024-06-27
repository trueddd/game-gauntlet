package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.Radio
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RadioItemTest : EventGateTest() {

    @Test
    fun `receive test`() = runTest {
        val user = getRandomPlayerName()
        val item = Radio.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, effectsOf(user).size)
        assertIs<Radio>(effectsOf(user).first())
    }

    @Test
    fun `two game test`() = runTest {
        val user = getRandomPlayerName()
        val item = Radio.create()
        handleAction(ItemReceive(user, item))
        handleAction(BoardMove(user, diceValue = 1))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertIs<Radio>(effectsOf(user).first())
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).isEmpty())
    }

    @Test
    fun `one game test`() = runTest {
        val user = getRandomPlayerName()
        val item = Radio.create()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemReceive(user, item))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertIs<Radio>(effectsOf(user).first())
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).isEmpty())
    }
}
