package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ShoppingWithChatItemTest : EventGateTest() {

    @Test
    fun `basic test`() = runTest {
        val user = requireRandomParticipant()
        val previousState = eventGate.stateHolder.current
        handleAction(ItemReceive(user, ShoppingWithChat.create()))
        assertEquals(previousState, eventGate.stateHolder.current)
    }
}
