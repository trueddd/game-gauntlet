package com.github.trueddd.core

import com.github.trueddd.core.actions.*
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.data.Game
import com.github.trueddd.provideInputParser
import com.github.trueddd.utils.d6Range
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InputParsingTest {

    private val inputParser = provideInputParser()

    @Test
    fun `parsing - wrong 1`() {
        assertNull(inputParser.parse("qwe"))
    }

    @Test
    fun `parsing - wrong 2`() {
        assertNull(inputParser.parse("bey"))
    }

    @Test
    fun `parsing - wrong 3`() {
        assertNull(inputParser.parse("qwe123123"))
    }

    @Test
    fun `parsing - board move`() {
        val parsed = inputParser.parse("shizov:1")
        assertIs<BoardMove>(parsed)
        assertEquals("shizov", parsed.rolledBy.name)
        assertTrue(parsed.diceValue in d6Range)
    }

    @Test
    fun `parsing - item roll`() {
        val parsed = inputParser.parse("shizov:3")
        assertIs<ItemReceive>(parsed)
        assertEquals("shizov", parsed.receivedBy.name)
    }

    @Test
    fun `parsing - game drop`() {
        val parsed = inputParser.parse("shizov:2")
        assertIs<GameDrop>(parsed)
        assertEquals("shizov", parsed.rolledBy.name)
        assertTrue(parsed.diceValue in d6Range)
    }

    @Test
    fun `parsing - game roll 1`() {
        val parsed = inputParser.parse("shizov:6")
        assertIs<GameRoll>(parsed)
        assertEquals("shizov", parsed.participant.name)
    }

    @Test
    fun `parsing - game roll 2`() {
        val parsed = inputParser.parse("shizov:1")
        assertIsNot<GameRoll>(parsed)
    }

    @Test
    fun `parsing - item use 1`() {
        val parsed = inputParser.parse("shizov:4:1")
        assertIs<ItemUse>(parsed)
        assertEquals("shizov", parsed.usedBy.name)
        assertEquals(expected = "1", parsed.itemUid)
        assertEquals(expected = emptyList(), parsed.arguments)
    }

    @Test
    fun `parsing - item use 2`() {
        val parsed = inputParser.parse("shizov:4:1:2")
        assertIs<ItemUse>(parsed)
        assertEquals("shizov", parsed.usedBy.name)
        assertEquals(expected = "1", parsed.itemUid)
        assertEquals(expected = listOf("2"), parsed.arguments)
    }

    @Test
    fun `parsing - game status change 1`() {
        val parsed = inputParser.parse("shizov:5:1")
        assertIs<GameStatusChange>(parsed)
        assertEquals("shizov", parsed.participant.name)
        assertEquals(Game.Status.Finished, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 2`() {
        val parsed = inputParser.parse("shizov:5:0")
        assertIs<GameStatusChange>(parsed)
        assertEquals("shizov", parsed.participant.name)
        assertEquals(Game.Status.InProgress, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 3`() {
        val parsed = inputParser.parse("shizov:5:2")
        assertIs<GameStatusChange>(parsed)
        assertEquals("shizov", parsed.participant.name)
        assertEquals(Game.Status.Dropped, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 4`() {
        val parsed = inputParser.parse("shizov:5:3")
        assertIs<GameStatusChange>(parsed)
        assertEquals("shizov", parsed.participant.name)
        assertEquals(Game.Status.Rerolled, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 5`() {
        val parsed = inputParser.parse("shizov:5:10")
        assertIsNot<GameStatusChange>(parsed)
    }

    @Test
    fun `parsing - game status change 6`() {
        val parsed = inputParser.parse("5:1")
        assertIsNot<GameStatusChange>(parsed)
    }
}