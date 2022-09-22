package com.github.trueddd.utils

import kotlin.test.Test
import kotlin.test.assertTrue

internal class DiceKtTest {

    private val diceRange = 1..6

    @Test
    fun `roll dice`() {
        val rolls = List(20) { rollDice() }
        assertTrue { rolls.all { it in diceRange } }
    }
}