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
    fun `roll game once`() = runTest {
        val participant = requireRandomParticipant()
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameRoll}")
        assertNotEquals(illegal = null, lastGameOf(participant))
    }

    @Test
    fun `roll game twice`() = runTest {
        val participant = requireRandomParticipant()
        handleAction(GameRoll(participant, Game.Id(0)))
        val currentGame = stateOf(participant).currentGame
        handleAction(GameRoll(participant, Game.Id(1)))
        assertEquals(currentGame, stateOf(participant).currentGame)
        assertEquals(Game.Id(0), stateOf(participant).gameHistory.firstOrNull()?.game?.id)
    }

    @Test
    fun `roll game - complete - move & roll game`() = runTest {
        val participant = requireRandomParticipant()
        handleAction(BoardMove(participant, diceValue = 5))
        handleAction(GameRoll(participant, Game.Id(0)))
        val firstGame = stateOf(participant).currentGame
        handleAction(GameStatusChange(participant, Game.Status.Finished))
        handleAction(BoardMove(participant, diceValue = 3))
        handleAction(GameRoll(participant, Game.Id(2)))
        assertEquals(firstGame?.game?.id, stateOf(participant).gameHistory.firstOrNull()?.game?.id)
        assertEquals(Game.Status.Finished, stateOf(participant).gameHistory.firstOrNull()?.status)
        assertEquals(Game.Status.InProgress, stateOf(participant).currentGame?.status)
    }
}
