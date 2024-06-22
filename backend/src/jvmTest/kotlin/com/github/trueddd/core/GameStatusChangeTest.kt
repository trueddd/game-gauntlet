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
        val participant = getRandomPlayerName()
        eventGate.parseAndHandle("$participant:${Action.Key.GameStatusChange}:${Game.Status.Dropped.ordinal}")
        assertEquals(expected = true, gamesOf(participant).isEmpty())
    }

    @Test
    fun `change status successfully`() = runTest {
        val participant = getRandomPlayerName()
        eventGate.parseAndHandle("$participant:${Action.Key.BoardMove}")
        eventGate.parseAndHandle("$participant:${Action.Key.GameRoll}")
        eventGate.parseAndHandle("$participant:${Action.Key.GameStatusChange}:${Game.Status.Finished.ordinal}")
        assertEquals(expected = false, gamesOf(participant).isEmpty())
        assertEquals(Game.Status.Finished, stateOf(participant).currentGame?.status)
    }
}
