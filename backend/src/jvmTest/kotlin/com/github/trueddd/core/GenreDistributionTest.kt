package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.data.GlobalState
import com.github.trueddd.map.Genre
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class GenreDistributionTest : EventGateTest() {

    @RepeatedTest(10)
    fun `genre sectors distribution`() = runTest {
        repeat (GlobalState.STINT_COUNT) { stintIndex ->
            val stint = eventGate.stateHolder.current.getStint(stintIndex)
            assertEquals(expected = Genre.Special, stint.last())
            assertContentEquals(expected = Genre.entries.sorted(), stint.sorted())
        }
    }
}
