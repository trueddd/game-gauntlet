package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HaveATryItemTest : EventGateTest() {

    @Test
    fun `using item`() = runTest {
        val user = requireRandomParticipant()
        val item = HaveATry.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).count { it is HaveATry })
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
    }
}
