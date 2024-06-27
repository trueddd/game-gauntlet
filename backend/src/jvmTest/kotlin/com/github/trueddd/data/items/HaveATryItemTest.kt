package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.HaveATry
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HaveATryItemTest : EventGateTest() {

    @Test
    fun `using item`() = runTest {
        val user = getRandomPlayerName()
        val item = HaveATry.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).count { it is HaveATry })
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
    }
}
