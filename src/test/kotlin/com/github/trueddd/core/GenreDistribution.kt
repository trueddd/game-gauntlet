package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class GenreDistribution : EventGateTest() {

    @RepeatedTest(10)
    fun `genre sectors distribution`() = runTest {
        for (stintIndex in 0 until GlobalState.STINT_COUNT) {
            val stint = eventGate.stateHolder.current.gameGenreDistribution.getStint(stintIndex)
            assertEquals(expected = Game.Genre.Special, stint.last())
            assertContentEquals(expected = Game.Genre.entries.sorted(), stint.sorted())
        }
        val layout = Json.encodeToString(GameGenreDistribution.serializer, eventGate.stateHolder.current.gameGenreDistribution)
            .replace(Game.Genre.Special.ordinal.toString(), "[]")
        println("Generated layout: $layout")
    }
}
