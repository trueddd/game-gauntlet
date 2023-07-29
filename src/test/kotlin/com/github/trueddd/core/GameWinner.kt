package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Participant
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameWinner : EventGateTest() {

    private suspend fun makeMove(userName: String) {
        eventGate.parseAndHandleSuspend("${Action.Commands.BOARD_MOVE} $userName")
        eventGate.parseAndHandleSuspend("${Action.Commands.GAME_ROLL} $userName")
        eventGate.parseAndHandleSuspend("${Action.Commands.GAME_STATUS_CHANGE} $userName 1")
    }

    private suspend fun makeMovesUntilFinish(userName: String) {
        flow {
            while (currentCoroutineContext().isActive) {
                makeMove(userName)
                emit(eventGate.stateHolder.current[userName]?.position)
            }
        }
            .onEach { println("position: $it") }
            .filterNotNull()
            .takeWhile { it < eventGate.stateHolder.current.boardLength }
            .collect()
    }

    @Test
    fun `basic winner test`() = runTest {
        makeMovesUntilFinish("shizov")
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }

    @Test
    fun `single winner test`() = runTest {
        makeMovesUntilFinish("shizov")
        makeMovesUntilFinish("keli")
        assertEquals(Participant("shizov"), eventGate.stateHolder.current.winner)
    }
}
