package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.data.Game
import com.github.trueddd.utils.rollDice
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BoardSteps : EventGateTest() {

    @Test
    fun `10 steps at once`() = runTest {
        val participant = requireParticipant("shizov")
        repeat(10) {
            eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        }
        assertEquals(expected = 1, eventGate.stateHolder.current.players[participant]?.stepsCount)
    }

    @Test
    fun `steps not available after board move`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.boardMoveAvailable)
    }

    @Test
    fun `step - game - step`() = runTest {
        val participant = requireParticipant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        eventGate.eventManager.suspendConsumeAction(GameRoll(participant, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(participant, Game.Status.Finished))
        assertEquals(expected = true, eventGate.stateHolder.current.players[participant]?.boardMoveAvailable)
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.boardMoveAvailable)
    }
}
