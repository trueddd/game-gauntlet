package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameWinner : EventGateTest() {

    private suspend fun makeMove(userName: String) {
        eventGate.parseAndHandleSuspend("$userName:${Action.Key.BoardMove}")
        eventGate.parseAndHandleSuspend("$userName:${Action.Key.GameRoll}")
        eventGate.parseAndHandleSuspend("$userName:${Action.Key.GameStatusChange}:1")
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
        assertEquals(requireParticipant("shizov"), eventGate.stateHolder.current.winner)
    }

    @Test
    fun `single winner test`() = runTest {
        makeMovesUntilFinish("shizov")
        makeMovesUntilFinish("keli")
        assertEquals(requireParticipant("shizov"), eventGate.stateHolder.current.winner)
    }
}
