package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GameWinner {

    private val eventGate = provideEventGate()

    @Test
    fun `basic winner test`() = runBlocking {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("move shizov")
            eventGate.parseAndHandleSuspend("roll-game shizov")
            eventGate.parseAndHandleSuspend("game shizov 1") // set game status to finished
        }
        assertEquals(eventGate.stateHolder.current.winner, Participant("shizov"))
    }

    @Test
    fun `single winner test`() = runBlocking {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("move shizov")
            eventGate.parseAndHandleSuspend("roll-game shizov")
            eventGate.parseAndHandleSuspend("game shizov 1") // set game status to finished
        }
        while (eventGate.stateHolder.current["keli"]?.position != eventGate.stateHolder.current.boardLength) {
            eventGate.parseAndHandleSuspend("move keli")
            eventGate.parseAndHandleSuspend("roll-game keli")
            eventGate.parseAndHandleSuspend("game keli 1") // set game status to finished
        }
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }
}
