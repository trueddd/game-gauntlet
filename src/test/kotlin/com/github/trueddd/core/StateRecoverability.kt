package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
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
            "${Action.Commands.BoardMove} shizov",
            "${Action.Commands.GameRoll} shizov",
            "${Action.Commands.BoardMove} solll",
            "${Action.Commands.ItemReceive} shizov",
            "${Action.Commands.GameDrop} shizov",
            "${Action.Commands.GameRoll} shizov",
            "${Action.Commands.GameStatusChange} shizov 1",
            "${Action.Commands.BoardMove} shizov",
            "${Action.Commands.BoardMove} keli",
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
