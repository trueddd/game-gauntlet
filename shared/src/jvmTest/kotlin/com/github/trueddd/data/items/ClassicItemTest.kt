package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClassicItemTest : EventGateTest() {

    @Test
    fun `ensure no buff`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, Classic.create()))
        eventGate.stateHolder.current.players.forEach { (_, state) ->
            assertEquals(expected = 0, state.modifiersSum)
        }
    }

    @Test
    fun `ensure buff received by everyone`() = runTest {
        val user = requireRandomParticipant()
        handleAction(GameRoll(user, Game.Id(5)))
        handleAction(ItemReceive(user, Classic.create()))
        eventGate.stateHolder.current.players.forEach { (_, state) ->
            assertEquals(expected = 1, state.modifiersSum)
            assertIs<WheelItem.Effect.Buff>(state.effects.first())
        }
    }

    @Test
    fun `buff removal after next move`() = runTest {
        val user = requireRandomParticipant()
        handleAction(GameRoll(user, Game.Id(5)))
        handleAction(ItemReceive(user, Classic.create()))
        handleAction(BoardMove(user, diceValue = 5))
        assertEquals(expected = 6, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }
}
