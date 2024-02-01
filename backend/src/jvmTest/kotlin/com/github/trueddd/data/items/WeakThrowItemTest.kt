package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.WeakThrow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WeakThrowItemTest : EventGateTest() {

    @Test
    fun `weak throw on 5`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, WeakThrow.create()))
        handleAction(BoardMove(user, diceValue = 5))
        assertEquals(expected = 4, positionOf(user))
    }

    @Test
    fun `weak throw on 1`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, WeakThrow.create()))
        handleAction(BoardMove(user, diceValue = 1))
        assertEquals(expected = 1,positionOf(user))
    }
}
