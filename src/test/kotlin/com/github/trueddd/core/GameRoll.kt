package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GameRoll : EventGateTest() {

    @Test
    fun `roll game once`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameRoll}")
        assertNotEquals(illegal = null, eventGate.stateHolder.current.players[participant]?.currentGameEntry)
    }

    @Test
    fun `roll game twice`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(GameRoll(participant, Game.Id(0)))
        val currentGame = eventGate.stateHolder.current.players[participant]?.currentGameEntry
        eventGate.eventManager.suspendConsumeAction(GameRoll(participant, Game.Id(1)))
        assertEquals(expected = currentGame, eventGate.stateHolder.current.players[participant]?.currentGameEntry)
        assertEquals(expected = Game.Id(0), eventGate.stateHolder.current.players[participant]?.gameHistory?.firstOrNull()?.game?.id)
    }

    @Test
    fun `roll game - complete - move & roll game`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, 5))
        eventGate.eventManager.suspendConsumeAction(GameRoll(participant, Game.Id(0)))
        val firstGame = eventGate.stateHolder.current.players[participant]?.currentGameEntry
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(participant, Game.Status.Finished))
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, 3))
        eventGate.eventManager.suspendConsumeAction(GameRoll(participant, Game.Id(2)))
        assertEquals(expected = firstGame?.game?.id, eventGate.stateHolder.current.players[participant]?.gameHistory?.firstOrNull()?.game?.id)
        assertEquals(expected = Game.Status.Finished, eventGate.stateHolder.current.players[participant]?.gameHistory?.firstOrNull()?.status)
        assertEquals(expected = Game.Status.InProgress, eventGate.stateHolder.current.players[participant]?.currentGameEntry?.status)
    }
}
