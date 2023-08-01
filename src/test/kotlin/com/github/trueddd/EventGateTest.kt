package com.github.trueddd

import com.github.trueddd.core.EventGate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class EventGateTest {

    protected val eventGate: EventGate = provideEventGate()

    protected fun requireParticipant(userName: String) = eventGate.stateHolder[userName]!!

    @BeforeEach
    fun startEventGate() {
        eventGate.start()
    }

    @AfterEach
    fun stopEventGate() {
        eventGate.stop()
    }
}
