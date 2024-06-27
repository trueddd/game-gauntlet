package com.github.trueddd.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PowerSetTest {

    @Test
    fun checkRealPowerSet() {
        val items = listOf(1, 2, 3)
        val expected = listOf(
            listOf(),
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(1, 2),
            listOf(1, 3),
            listOf(2, 3),
            listOf(1, 2, 3)
        )
        val powerSet = items.powerSet()
        assertEquals(expected = expected.size, actual = powerSet.size)
        assertTrue { powerSet.all { it in expected } }
    }

    @Test
    fun checkFakePowerSet() {
        val items = listOf(1, 2)
        val expected = listOf(
            listOf(),
            listOf(1),
            listOf(2),
        )
        val powerSet = items.powerSet()
        assertFalse { powerSet.all { it in expected } }
    }
}
