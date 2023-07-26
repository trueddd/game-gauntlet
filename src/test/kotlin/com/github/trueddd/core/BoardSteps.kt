package com.github.trueddd.core

import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.data.Game
import com.github.trueddd.data.Participant
import com.github.trueddd.provideEventGate
import com.github.trueddd.utils.rollDice
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BoardSteps {

    private val eventGate = provideEventGate()

    @Test
    fun `10 steps at once`() = runBlocking {
        val participant = Participant("shizov")
        repeat(10) {
            eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        }
        assertEquals(expected = 1, eventGate.stateHolder.current.players[participant]?.stepsCount)
    }

    @Test
    fun `steps not available after board move`() = runBlocking {
        val participant = Participant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.boardMoveAvailable)
    }

    @Test
    fun `step - game - step`() = runBlocking {
        val participant = Participant("shizov")
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        eventGate.eventManager.suspendConsumeAction(GameRoll(participant, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(participant, Game.Status.Finished))
        assertEquals(expected = true, eventGate.stateHolder.current.players[participant]?.boardMoveAvailable)
        eventGate.eventManager.suspendConsumeAction(BoardMove(participant, rollDice()))
        assertEquals(expected = false, eventGate.stateHolder.current.players[participant]?.boardMoveAvailable)
    }
}