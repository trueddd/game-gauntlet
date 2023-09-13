package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.Log
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameWinnerTest : EventGateTest() {

    companion object {
        private const val TAG = "GameWinnerTest"
    }

    private suspend fun makeMove(userName: String) {
        eventGate.parseAndHandleSuspend("$userName:${Action.Key.BoardMove}")
        eventGate.parseAndHandleSuspend("$userName:${Action.Key.GameRoll}")
        eventGate.parseAndHandleSuspend("$userName:${Action.Key.GameStatusChange}:1")
    }

    private suspend fun makeMovesUntilFinish(player: Participant) {
        flow {
            while (currentCoroutineContext().isActive) {
                makeMove(player.name)
                emit(positionOf(player))
            }
        }
            .onEach { Log.info(TAG, "position: $it") }
            .filterNotNull()
            .takeWhile { it < eventGate.stateHolder.current.boardLength }
            .collect()
    }

    @Test
    fun `basic winner test`() = runTest {
        val player = requireRandomParticipant()
        makeMovesUntilFinish(player)
        assertEquals(player, eventGate.stateHolder.current.winner)
    }

    @Test
    fun `single winner test`() = runTest {
        val (player1, player2) = requireParticipants()
        makeMovesUntilFinish(player1)
        makeMovesUntilFinish(player2)
        assertEquals(player1, eventGate.stateHolder.current.winner)
    }
}
