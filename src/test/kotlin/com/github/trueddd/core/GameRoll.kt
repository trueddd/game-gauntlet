package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GameRoll {

    private val eventGate = provideEventGate()

    @Test
    fun `roll game once`() = runBlocking {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("roll-game shizov")
        assertNotEquals(illegal = null, eventGate.stateHolder.current.players[participant]?.currentGameEntry)
    }

    @Test
    fun `roll game twice`() = runBlocking {
        val participant = Participant("shizov")
        eventGate.parseAndHandleSuspend("roll-game shizov")
        val currentGame = eventGate.stateHolder.current.players[participant]?.currentGameEntry
        eventGate.parseAndHandleSuspend("roll-game shizov")
        assertEquals(expected = currentGame, eventGate.stateHolder.current.players[participant]?.currentGameEntry)
    }
}
