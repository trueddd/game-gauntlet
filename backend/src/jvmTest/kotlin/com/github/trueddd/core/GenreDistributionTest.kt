package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.serialization
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class GenreDistributionTest : EventGateTest() {

    companion object {
        private const val TAG = "GenreDistributionTest"
    }

    @RepeatedTest(10)
    fun `genre sectors distribution`() = runTest {
        for (stintIndex in 0 until GlobalState.STINT_COUNT) {
            val stint = eventGate.stateHolder.current.gameGenreDistribution.getStint(stintIndex)
            assertEquals(expected = Game.Genre.Special, stint.last())
            assertContentEquals(expected = Game.Genre.entries.sorted(), stint.sorted())
        }
        val layout = serialization.encodeToString(
            GameGenreDistribution.serializer(),
            eventGate.stateHolder.current.gameGenreDistribution
        ).replace(Game.Genre.Special.ordinal.toString(), "[]")
        Log.info(TAG, "Generated layout: $layout")
    }
}
