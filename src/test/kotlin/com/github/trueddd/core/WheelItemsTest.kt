package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.di.getItemFactoriesSet
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WheelItemsTest : EventGateTest() {

    @Test
    fun `items IDs uniqueness`() = runTest {
        val factories = getItemFactoriesSet()
        val ids = factories.map { it.itemId }
        assertEquals(ids.size, ids.distinct().size)
    }
}
