package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameStatus : EventGateTest() {

    @Test
    fun `change status with failure`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameStatusChange}:2")
        assertEquals(expected = true, eventGate.stateHolder.current.players[participant]?.gameHistory?.isEmpty())
    }

    @Test
    fun `change status successfully`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameRoll}")
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameStatusChange}:1")
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.gameHistory?.isEmpty())
        assertEquals(expected = Game.Status.Finished, eventGate.stateHolder.current.players[participant]?.currentGame?.status)
    }
}
