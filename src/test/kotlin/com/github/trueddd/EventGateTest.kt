package com.github.trueddd

import com.github.trueddd.core.EventGate
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.Participant
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class EventGateTest {

    protected val eventGate: EventGate = provideEventGate()

    protected fun requireParticipant(userName: String) = eventGate.stateHolder[userName]!!

    protected fun stateOf(participant: Participant) = eventGate.stateHolder.current[participant.name]!!
    protected fun effectsOf(participant: Participant) = stateOf(participant).effects
    protected fun inventoryOf(participant: Participant) = stateOf(participant).inventory
    protected fun positionOf(participant: Participant) = stateOf(participant).position
    protected fun lastGameOf(participant: Participant) = stateOf(participant).currentGame

    protected suspend fun handleAction(action: Action) = eventGate.eventManager.suspendConsumeAction(action)

    @BeforeEach
    fun startEventGate() {
        eventGate.start()
    }

    @AfterEach
    fun stopEventGate() {
        eventGate.stop()
    }
}
