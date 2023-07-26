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
internal class InputParsing {

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
        val parsed = inputParser.parse("move player")
        assertIs<BoardMove>(parsed)
        assertEquals("player", parsed.rolledBy.name)
        assertTrue(parsed.diceValue in d6Range)
    }

    @Test
    fun `parsing - item roll`() {
        val parsed = inputParser.parse("item player")
        assertIs<ItemReceive>(parsed)
        assertEquals("player", parsed.receivedBy.name)
    }

    @Test
    fun `parsing - game drop`() {
        val parsed = inputParser.parse("drop player")
        assertIs<GameDrop>(parsed)
        assertEquals("player", parsed.rolledBy.name)
        assertTrue(parsed.diceValue in d6Range)
    }

    @Test
    fun `parsing - game roll 1`() {
        val parsed = inputParser.parse("roll-game player")
        assertIs<GameRoll>(parsed)
        assertEquals("player", parsed.participant.name)
    }

    @Test
    fun `parsing - game roll 2`() {
        val parsed = inputParser.parse("roll player")
        assertIsNot<GameRoll>(parsed)
    }

    @Test
    fun `parsing - game status change 1`() {
        val parsed = inputParser.parse("game player 1")
        assertIs<GameStatusChange>(parsed)
        assertEquals("player", parsed.participant.name)
        assertEquals(Game.Status.Finished, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 2`() {
        val parsed = inputParser.parse("game player 0")
        assertIs<GameStatusChange>(parsed)
        assertEquals("player", parsed.participant.name)
        assertEquals(Game.Status.InProgress, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 3`() {
        val parsed = inputParser.parse("game player 2")
        assertIs<GameStatusChange>(parsed)
        assertEquals("player", parsed.participant.name)
        assertEquals(Game.Status.Dropped, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 4`() {
        val parsed = inputParser.parse("game player 3")
        assertIs<GameStatusChange>(parsed)
        assertEquals("player", parsed.participant.name)
        assertEquals(Game.Status.Rerolled, parsed.gameNewStatus)
    }

    @Test
    fun `parsing - game status change 5`() {
        val parsed = inputParser.parse("game player 10")
        assertIsNot<GameStatusChange>(parsed)
    }

    @Test
    fun `parsing - game status change 6`() {
        val parsed = inputParser.parse("game 1")
        assertIsNot<GameStatusChange>(parsed)
    }
}