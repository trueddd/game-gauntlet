package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.data.globalState
import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ActionTimeRangeTest : EventGateTest() {

    @BeforeEach
    override fun startEventGate() {
        // Manual state refresh before each test
    }

    @Test
    fun `handle action before start`() = runTest {
        eventGate.startNoLoad(globalState(
            startDateTime = (Clock.System.now() + 2.hours).toLocalDateTime(DefaultTimeZone)
        ))
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 5))
        assertEquals(expected = 0, positionOf(user))
    }

    @Test
    fun `handle action after end`() = runTest {
        eventGate.startNoLoad(globalState(
            startDateTime = (Clock.System.now() - 1.hours).toLocalDateTime(DefaultTimeZone),
            activePeriod = 5.minutes,
        ))
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 4))
        assertEquals(expected = 0, positionOf(user))
    }

    @Test
    fun `handle action in proper time range`() = runTest {
        eventGate.startNoLoad(globalState())
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 4))
        assertEquals(expected = 4, positionOf(user))
    }
}
