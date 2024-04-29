package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.Action
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.data.Game
import com.github.trueddd.utils.rollDice
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BoardMoveActionTest : EventGateTest() {

    @Test
    fun `10 steps at once`() = runTest {
        val participant = requireRandomParticipant()
        repeat(10) {
            handleAction(BoardMove(participant, rollDice()))
        }
        assertEquals(expected = 1, stateOf(participant).stepsCount)
    }

    @Test
    fun `steps not available after board move`() = runTest {
        val participant = requireRandomParticipant()
        handleAction(BoardMove(participant, rollDice()))
        assertEquals(expected = false, stateOf(participant).boardMoveAvailable)
    }

    @Test
    fun `step - game - step`() = runTest {
        val participant = requireRandomParticipant()
        handleAction(BoardMove(participant, rollDice()))
        handleAction(GameRoll(participant, Game.Id(1)))
        handleAction(GameStatusChange(participant, Game.Status.Finished))
        assertEquals(expected = true, stateOf(participant).boardMoveAvailable)
        handleAction(BoardMove(participant, rollDice()))
        assertEquals(expected = false, stateOf(participant).boardMoveAvailable)
    }

    @RepeatedTest(10)
    fun `step on passed dice value`() = runTest {
        val participant = requireRandomParticipant()
        val diceValue = 6
        eventGate.parseAndHandle("${participant.name}:${Action.Key.BoardMove}:$diceValue")
        assertEquals(expected = diceValue, positionOf(participant))
    }
}
