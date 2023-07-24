package com.github.trueddd.core

import com.github.trueddd.core.actions.*
import com.github.trueddd.provideInputParser
import com.github.trueddd.utils.d6Range
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InputParserTest {

    private val inputParser = provideInputParser()

    @Test
    fun `roll movement dice - wrong 1`() {
        assertNull(inputParser.parse("qwe"))
    }

    @Test
    fun `roll movement dice - wrong 2`() {
        assertNull(inputParser.parse("bey"))
    }

    @Test
    fun `roll movement dice - wrong 3`() {
        assertNull(inputParser.parse("qwe123123"))
    }

    @Test
    fun `roll movement dice - board move`() {
        val parsed = inputParser.parse("roll player") as? BoardMove
        assertEquals("player", parsed?.rolledBy?.name)
        assertTrue(parsed?.diceValue in d6Range)
    }

    @Test
    fun `roll movement dice - item roll`() {
        val parsed = inputParser.parse("item player") as? ItemReceive
        assertEquals("player", parsed?.receivedBy?.name)
    }

    @Test
    fun `roll movement dice - game drop`() {
        val parsed = inputParser.parse("drop player") as? GameDrop
        assertEquals("player", parsed?.rolledBy?.name)
        assertTrue(parsed?.diceValue in d6Range)
    }
}