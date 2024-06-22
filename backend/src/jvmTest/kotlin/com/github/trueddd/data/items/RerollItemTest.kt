package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.Reroll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RerollItemTest : EventGateTest() {

    @Test
    fun `test reroll use`() = runTest {
        val user = getRandomPlayerName()
        val initial = eventGate.stateHolder.current
        handleAction(ItemReceive(user, Reroll.create()))
        assertEquals(expected = initial, eventGate.stateHolder.current)
    }
}
