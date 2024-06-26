package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.data.Game
import com.github.trueddd.items.DontWannaPlayThis
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DontWannaPlayThisItemTest : EventGateTest() {

    @Test
    fun `reroll game with item`() = runTest {
        val user = getRandomPlayerName()
        val item = DontWannaPlayThis.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 1, gamesOf(user).size)
        assertEquals(expected = Game.Status.Rerolled, stateOf(user).currentGame?.status)
    }

    @Test
    fun `reroll game - check stats`() = runTest {
        val user = getRandomPlayerName()
        val item = DontWannaPlayThis.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(ItemReceive(user, item))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = Game.Status.Rerolled, stateOf(user).currentGame?.status)
        assertEquals(expected = 1, historyOf(user).statistics.rerolledGames)
        assertEquals(expected = 0, historyOf(user).statistics.finishedGames)
    }
}
