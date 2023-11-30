package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameWinnerTest : EventGateTest() {

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
