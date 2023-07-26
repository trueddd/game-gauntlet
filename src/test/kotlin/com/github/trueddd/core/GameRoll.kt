package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Participant
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GameRoll {

    private val eventGate = provideEventGate()

    @Test
    fun `roll game once`() = runTest {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} ${participant.name}")
        assertNotEquals(illegal = null, eventGate.stateHolder.current.players[participant]?.currentGameEntry)
    }

    @Test
    fun `roll game twice`() = runTest {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} ${participant.name}")
        val currentGame = eventGate.stateHolder.current.players[participant]?.currentGameEntry
        eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} ${participant.name}")
        assertEquals(expected = currentGame, eventGate.stateHolder.current.players[participant]?.currentGameEntry)
    }
}
