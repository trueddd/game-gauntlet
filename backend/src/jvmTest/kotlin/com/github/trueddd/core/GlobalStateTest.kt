package com.github.trueddd.core

import com.github.trueddd.data.globalState
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalStateTest {

    @Test
    fun `update player state`() = runTest {
        val state = globalState()
        val user = state.players.first()
        val result = state.updatePlayer(user) {
            it.copy(position = 10)
        }
        assertEquals(expected = 10, result.positionOf(user))
    }
}
