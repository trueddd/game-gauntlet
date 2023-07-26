package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
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
            eventGate.parseAndHandleSuspend("${Action.Commands.BoardMove} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameStatusChange} shizov 1") // set game status to finished
        }
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }

    @Test
    fun `single winner test`() = runBlocking {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("${Action.Commands.BoardMove} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameStatusChange} shizov 1") // set game status to finished
        }
        while (eventGate.stateHolder.current["keli"]?.position != eventGate.stateHolder.current.boardLength) {
            eventGate.parseAndHandleSuspend("${Action.Commands.BoardMove} keli")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} keli")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameStatusChange} keli 1") // set game status to finished
        }
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }
}
