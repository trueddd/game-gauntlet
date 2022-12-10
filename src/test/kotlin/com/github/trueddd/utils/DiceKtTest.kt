package com.github.trueddd.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class DiceKtTest {

    @Test
    fun `roll dice`() {
        repeat(20) {
            assertTrue(rollDice() in d6Range)
        }
    }
}