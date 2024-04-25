package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.data.Game
import com.github.trueddd.items.LuckyThrow
import com.github.trueddd.items.WheelItem
import com.github.trueddd.utils.rollDice
import com.github.trueddd.utils.serialization
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LuckyThrowItemTest : EventGateTest() {

    @Test
    fun `acquire item`() = runTest {
        val user = requireRandomParticipant()
        val item = LuckyThrow.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, pendingEventsOf(user).size)
        assertIs<LuckyThrow>(pendingEventsOf(user).first())
    }

    @Test
    fun `get and use item`() = runTest {
        val user = requireRandomParticipant()
        val item = LuckyThrow.create()
        val diceValue = rollDice()
        val genre = eventGate.stateHolder.current.gameGenreDistribution.genreAtPosition(diceValue)
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf(serialization.encodeToString(Game.Genre.serializer(), genre))))
        assertEquals(expected = 1, effectsOf(user).size)
        assertIs<WheelItem.Effect.Buff>(effectsOf(user).first())
    }

    @Test
    fun `get and use item - successfully`() = runTest {
        val user = requireRandomParticipant()
        val item = LuckyThrow.create()
        val diceValue = rollDice()
        val genre = eventGate.stateHolder.current.gameGenreDistribution.genreAtPosition(diceValue)
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf(serialization.encodeToString(Game.Genre.serializer(), genre))))
        assertEquals(expected = 1, effectsOf(user).size)
        assertIs<WheelItem.Effect.Buff>(effectsOf(user).first())
        handleAction(BoardMove(user, diceValue))
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(genre, eventGate.stateHolder.current.gameGenreDistribution.genreAtPosition(positionOf(user)))
        assertTrue(stateOf(user).boardMoveAvailable)
    }

    @Test
    fun `get and use item - unlucky`() = runTest {
        val user = requireRandomParticipant()
        val item = LuckyThrow.create()
        val diceValue = 3
        val genre = eventGate.stateHolder.current.gameGenreDistribution.genreAtPosition(2)
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(ItemUse(user, item.uid, listOf(serialization.encodeToString(Game.Genre.serializer(), genre))))
        assertEquals(expected = 1, effectsOf(user).size)
        assertIs<WheelItem.Effect.Buff>(effectsOf(user).first())
        handleAction(BoardMove(user, diceValue))
        assertEquals(expected = 0, effectsOf(user).size)
        assertFalse(stateOf(user).boardMoveAvailable)
    }
}
