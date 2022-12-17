package com.github.trueddd.utils

import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertContains

internal class DiceKtTest {

    @RepeatedTest(20)
    fun `roll dice`() {
        assertContains(d6Range, rollDice())
    }
}