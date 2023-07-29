package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.Action
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertEquals

class StateRecoverability : EventGateTest() {

    @RepeatedTest(10)
    fun `save, load & compare`() = runTest {
        val actionsSequence = sequenceOf(
            "${Action.Commands.BOARD_MOVE} shizov",
            "${Action.Commands.GAME_ROLL} shizov",
            "${Action.Commands.BOARD_MOVE} solll",
            "${Action.Commands.ITEM_RECEIVE} shizov",
            "${Action.Commands.GAME_DROP} shizov",
            "${Action.Commands.GAME_ROLL} shizov",
            "${Action.Commands.GAME_STATUS_CHANGE} shizov 1",
            "${Action.Commands.BOARD_MOVE} shizov",
            "${Action.Commands.BOARD_MOVE} keli",
        )
        actionsSequence.forEach {
            eventGate.parseAndHandleSuspend(it)
        }
        eventGate.historyHolder.save(eventGate.stateHolder.current)
        eventGate.eventManager.stopHandling()
        val restored = eventGate.historyHolder.load()
        assertEquals(eventGate.stateHolder.globalStateFlow.value, restored)
    }
}
