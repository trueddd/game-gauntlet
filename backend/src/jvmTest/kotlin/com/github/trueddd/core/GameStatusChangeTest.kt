package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.Action
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameStatusChangeTest : EventGateTest() {

    @Test
    fun `change status with failure`() = runTest {
        val participant = requireRandomParticipant()
        eventGate.parseAndHandle("${participant.name}:${Action.Key.GameStatusChange}:2")
        assertEquals(expected = true, gamesOf(participant).isEmpty())
    }

    @Test
    fun `change status successfully`() = runTest {
        val participant = requireRandomParticipant()
        eventGate.parseAndHandle("${participant.name}:${Action.Key.BoardMove}")
        eventGate.parseAndHandle("${participant.name}:${Action.Key.GameRoll}")
        eventGate.parseAndHandle("${participant.name}:${Action.Key.GameStatusChange}:1")
        assertEquals(expected = false, gamesOf(participant).isEmpty())
        assertEquals(Game.Status.Finished, stateOf(participant).currentGame?.status)
    }
}
