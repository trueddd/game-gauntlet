package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.Action
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GameRollActionTest : EventGateTest() {

    @Test
    fun `roll game on start`() = runTest {
        val participant = getRandomPlayerName()
        eventGate.parseAndHandle("$participant:${Action.Key.GameRoll}")
        assertEquals(expected = null, lastGameOf(participant))
    }

    @Test
    fun `roll game once`() = runTest {
        val participant = getRandomPlayerName()
        handleAction(BoardMove(participant, diceValue = 3))
        handleAction(GameRoll(participant, Game.Id(1)))
        assertNotEquals(illegal = null, lastGameOf(participant))
    }

    @Test
    fun `roll game - given id`() = runTest {
        val participant = getRandomPlayerName()
        handleAction(BoardMove(participant, diceValue = 3))
        eventGate.parseAndHandle("$participant:${Action.Key.GameRoll}:1")
        assertEquals(expected = Game.Id(1), lastGameOf(participant)?.game?.id)
    }

    @Test
    fun `roll game twice`() = runTest {
        val participant = getRandomPlayerName()
        handleAction(BoardMove(participant, diceValue = 2))
        handleAction(GameRoll(participant, Game.Id(0)))
        val currentGame = stateOf(participant).currentGame
        handleAction(GameRoll(participant, Game.Id(1)))
        assertEquals(currentGame, stateOf(participant).currentGame)
        assertEquals(Game.Id(0), gamesOf(participant).firstOrNull()?.game?.id)
    }

    @Test
    fun `roll game - complete - move & roll game`() = runTest {
        val participant = getRandomPlayerName()
        handleAction(BoardMove(participant, diceValue = 5))
        handleAction(GameRoll(participant, Game.Id(0)))
        val firstGame = stateOf(participant).currentGame
        handleAction(GameStatusChange(participant, Game.Status.Finished))
        handleAction(BoardMove(participant, diceValue = 3))
        handleAction(GameRoll(participant, Game.Id(2)))
        assertEquals(firstGame?.game?.id, gamesOf(participant).firstOrNull()?.game?.id)
        assertEquals(Game.Status.Finished, gamesOf(participant).firstOrNull()?.status)
        assertEquals(Game.Status.InProgress, stateOf(participant).currentGame?.status)
    }
}
