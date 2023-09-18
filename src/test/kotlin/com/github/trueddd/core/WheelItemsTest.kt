package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.data.items.WheelItem
import com.github.trueddd.di.getItemFactoriesSet
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WheelItemsTest : EventGateTest() {

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `items IDs uniqueness`() = runTest {
        val factories = getItemFactoriesSet() as Set<WheelItem.Factory>
        val ids = factories.map { it.itemId }
        assertEquals(ids.size, ids.distinct().size)
    }
}
