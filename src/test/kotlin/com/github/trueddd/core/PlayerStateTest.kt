package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.data.PlayerState
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlayerStateTest : EventGateTest() {

    @Test
    fun `stint calculation - 1`() = runTest {
        assertEquals(expected = 3, PlayerState.calculateStintIndex(position = 24))
    }

    @Test
    fun `stint calculation - 2`() = runTest {
        assertEquals(expected = 0, PlayerState.calculateStintIndex(position = 2))
    }

    @Test
    fun `stint calculation - 3`() = runTest {
        assertEquals(expected = 8, PlayerState.calculateStintIndex(position = 60))
    }

    @Test
    fun `stint calculation - 4`() = runTest {
        assertEquals(expected = 12, PlayerState.calculateStintIndex(position = 91))
    }

    @Test
    fun `stint calculation - 5`() = runTest {
        assertEquals(expected = 0, PlayerState.calculateStintIndex(position = 7))
    }
}
