package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PowerThrowItemTest : EventGateTest() {

    @Test
    fun `power throw on 3`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(BoardMove(user, diceValue = 3))
        assertEquals(expected = 4, positionOf(user))
    }

    @Test
    fun `power throw on 6`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, PowerThrow.create()))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 7, positionOf(user))
    }

    @Test
    fun `power throw overflow`() = runTest {
        val user = requireRandomParticipant()
        repeat(5) {
            handleAction(ItemReceive(user, PowerThrow.create(chargesLeft = 1)))
        }
        handleAction(BoardMove(user, 6))
        assertEquals(expected = 10, positionOf(user))
        assertEquals(expected = 1, effectsOf(user).size)
    }

    @Test
    fun `power throw removal after move`() = runTest {
        val user = requireRandomParticipant()
        repeat(5) {
            handleAction(ItemReceive(user, PowerThrow.create()))
        }
        handleAction(BoardMove(user, diceValue = 5))
        assertEquals(expected = 10, positionOf(user))
        assertEquals(expected = 0, effectsOf(user).size)
    }

    @Test
    fun `power throw with charges`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, PowerThrow.create(chargesLeft = 2)))
        handleAction(BoardMove(user, diceValue = 6))
        assertEquals(expected = 7, positionOf(user))
        assertEquals(expected = 1, effectsOf(user).filterIsInstance<PowerThrow>().firstOrNull()?.chargesLeft)
    }
}
