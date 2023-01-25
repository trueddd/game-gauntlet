package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GameWinnerTest {

    private val eventGate = provideEventGate()

    @Test
    fun `basic winner test`() = runBlocking {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("roll shizov")
        }
        assertEquals(eventGate.stateHolder.current.winner, Participant("shizov"))
    }

    @Test
    fun `single winner test`() = runBlocking {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("roll shizov")
        }
        while (eventGate.stateHolder.current["keli"]?.position != eventGate.stateHolder.current.boardLength) {
            eventGate.parseAndHandleSuspend("roll keli")
        }
        assertEquals(eventGate.stateHolder.current["keli"]?.position, eventGate.stateHolder.current.boardLength)
        assertEquals(eventGate.stateHolder.current.winner, Participant("shizov"))
    }
}
