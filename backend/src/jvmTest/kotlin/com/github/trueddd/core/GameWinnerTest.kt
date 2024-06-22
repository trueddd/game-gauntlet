package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameWinnerTest : EventGateTest() {

    @Test
    fun `basic winner test`() = runTest {
        val player = getRandomPlayerName()
        makeMovesUntilFinish(player)
        assertEquals(player, eventGate.stateHolder.current.stateSnapshot.winner)
    }

    @Test
    fun `single winner test`() = runTest {
        val (player1, player2) = getPlayerNames()
        makeMovesUntilFinish(player1)
        makeMovesUntilFinish(player2)
        assertEquals(player1, eventGate.stateHolder.current.stateSnapshot.winner)
    }
}
