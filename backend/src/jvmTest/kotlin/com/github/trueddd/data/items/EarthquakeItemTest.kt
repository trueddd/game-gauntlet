package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.Earthquake
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EarthquakeItemTest : EventGateTest() {

    @Test
    fun `quake 1`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, Earthquake.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.forEach { (_, playerState) ->
            assertEquals(expected = 1, playerState.position)
        }
    }

    @Test
    fun `quake 2`() = runTest {
        val (user1, user2) = requireParticipants()
        handleAction(BoardMove(user1, diceValue = 2))
        handleAction(BoardMove(user2, diceValue = 3))
        handleAction(ItemReceive(user1, Earthquake.create()))
        assertEquals(expected = 3, positionOf(user1))
        assertEquals(expected = 2, positionOf(user2))
    }
}
