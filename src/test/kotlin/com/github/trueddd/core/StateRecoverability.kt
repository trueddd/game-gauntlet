package com.github.trueddd.core

import com.github.trueddd.provideEventGate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class StateRecoverability {

    private val eventGate = provideEventGate()

    @RepeatedTest(10)
    fun `save, load & compare`() = runBlocking {
        val actionsSequence = sequenceOf(
            "move shizov",
            "roll-game shizov",
            "move solll",
            "item shizov",
            "drop shizov",
            "roll-game shizov",
            "game shizov 1",
            "move shizov",
            "move keli",
        )
        actionsSequence.forEach {
            eventGate.parseAndHandleSuspend(it)
        }
        eventGate.historyHolder.save()
        eventGate.eventManager.stopHandling()
        val restored = eventGate.historyHolder.load()
        assertEquals(eventGate.stateHolder.globalStateFlow.value, restored)
    }
}
