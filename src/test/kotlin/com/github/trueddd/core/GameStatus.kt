package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Game
import com.github.trueddd.data.Participant
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameStatus : EventGateTest() {

    @Test
    fun `change status with failure`() = runTest {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("${Action.Commands.GAME_STATUS_CHANGE} ${participant.name} 2")
        assertEquals(expected = true, eventGate.stateHolder.current.players[participant]?.gameHistory?.isEmpty())
    }

    @Test
    fun `change status successfully`() = runTest {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("${Action.Commands.GAME_ROLL} ${participant.name}")
        eventGate.parseAndHandleSuspend("${Action.Commands.GAME_STATUS_CHANGE} ${participant.name} 1")
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.gameHistory?.isEmpty())
        assertEquals(expected = Game.Status.Finished, eventGate.stateHolder.current.players[participant]?.currentGameEntry?.status)
    }
}
