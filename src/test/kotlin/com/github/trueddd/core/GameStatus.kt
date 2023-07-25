package com.github.trueddd.core

import com.github.trueddd.data.Game
import com.github.trueddd.data.Participant
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GameStatus {

    private val eventGate = provideEventGate()

    @Test
    fun `change status with failure`() = runBlocking {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("game shizov 2")
        assertEquals(expected = true, eventGate.stateHolder.current.players[participant]?.gameHistory?.isEmpty())
    }

    @Test
    fun `change status successfully`() = runBlocking {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("roll-game shizov")
        eventGate.parseAndHandleSuspend("game shizov 1")
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.gameHistory?.isEmpty())
        assertEquals(expected = Game.Status.Finished, eventGate.stateHolder.current.players[participant]?.currentGameEntry?.status)
    }
}
