package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameDrop
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.IWouldBeatIt
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IWouldBeatItItemTest : EventGateTest() {

    @Test
    fun `empty drop list`() = runTest {
        val user1 = getRandomPlayerName()
        handleAction(ItemReceive(user1, IWouldBeatIt.create()))
        handleAction(BoardMove(user1, diceValue = 3))
        handleAction(GameRoll(user1, Game.Id(1)))
        assertEquals(expected = 1, gamesOf(user1).size)
        assertTrue(eventGate.stateHolder.current.getDroppedGames().isEmpty())
    }

    @Test
    fun `get game from drop list`() = runTest {
        val (user1, user2, user3) = getPlayerNames()
        handleAction(BoardMove(user1, diceValue = 3))
        handleAction(BoardMove(user2, diceValue = 3))
        handleAction(BoardMove(user3, diceValue = 4))
        handleAction(GameRoll(user1, Game.Id(1)))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameRoll(user3, Game.Id(3)))
        handleAction(GameDrop(user1, diceValue = 1))
        handleAction(GameDrop(user2, diceValue = 1))
        handleAction(GameDrop(user3, diceValue = 1))
        handleAction(ItemReceive(user1, IWouldBeatIt.create()))
        assertTrue(eventGate.stateHolder.current.getDroppedGames().isNotEmpty())
        handleAction(GameRoll(user1, Game.Id(3)))
        assertTrue(lastGameOf(user1)?.game?.id in eventGate.stateHolder.current.getDroppedGames())
    }

    @Test
    fun `get game not from drop list`() = runTest {
        val (user1, user2, user3) = getPlayerNames()
        handleAction(BoardMove(user1, diceValue = 3))
        handleAction(BoardMove(user2, diceValue = 3))
        handleAction(BoardMove(user3, diceValue = 4))
        handleAction(GameRoll(user1, Game.Id(1)))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameRoll(user3, Game.Id(3)))
        handleAction(GameDrop(user1, diceValue = 1))
        handleAction(GameDrop(user2, diceValue = 1))
        handleAction(GameDrop(user3, diceValue = 1))
        handleAction(ItemReceive(user1, IWouldBeatIt.create()))
        assertTrue(eventGate.stateHolder.current.getDroppedGames().isNotEmpty())
        handleAction(GameRoll(user1, Game.Id(4)))
        assertEquals(expected = 1, gamesOf(user1).size)
    }
}
