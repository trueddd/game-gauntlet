package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.Classic
import com.github.trueddd.items.WheelItem
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
        eventGate.stateHolder.current.stateSnapshot.playersState.forEach { (_, state) ->
            assertEquals(expected = 0, state.modifiersSum)
        }
    }

    @Test
    fun `ensure buff received by everyone`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(GameRoll(user, Game.Id(Classic.FARM_FRENZY_ID_RANGE.random())))
        handleAction(ItemReceive(user, Classic.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.forEach { (_, state) ->
            assertEquals(expected = 1, state.modifiersSum)
            assertIs<WheelItem.Effect.Buff>(state.effects.first())
        }
    }

    @Test
    fun `buff removal after next move`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 2))
        handleAction(GameRoll(user, Game.Id(Classic.SUPER_COW_ID)))
        handleAction(ItemReceive(user, Classic.create()))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(BoardMove(user, diceValue = 3))
        assertEquals(expected = 6, positionOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }
}
