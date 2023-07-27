package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Participant
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameWinner : EventGateTest() {

    @Test
    fun `basic winner test`() = runTest {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("${Action.Commands.BoardMove} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameStatusChange} shizov 1") // set game status to finished
        }
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }

    @Test
    fun `single winner test`() = runTest {
        while (eventGate.stateHolder.current.winner == null) {
            eventGate.parseAndHandleSuspend("${Action.Commands.BoardMove} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} shizov")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameStatusChange} shizov 1") // set game status to finished
        }
        while (eventGate.stateHolder.current["keli"]!!.position < eventGate.stateHolder.current.boardLength) {
            eventGate.parseAndHandleSuspend("${Action.Commands.BoardMove} keli")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameRoll} keli")
            eventGate.parseAndHandleSuspend("${Action.Commands.GameStatusChange} keli 1") // set game status to finished
        }
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }
}
