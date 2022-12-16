package com.github.trueddd.core

import com.github.trueddd.provideActionHandlerRegistry
import com.github.trueddd.provideHistoryHolder
import com.github.trueddd.provideInputParser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StateRecoverabilityTest {

    private val stateHolder = StateHolder()
    private val actionHandlerRegistry = provideActionHandlerRegistry()
    private val inputParser = provideInputParser(stateHolder)
    private val eventManager = EventManager(actionHandlerRegistry, stateHolder)
    private val historyHolder = provideHistoryHolder(actionHandlerRegistry)

    @Test
    fun `test 1`() {
        runBlocking {
            val actionsSequence = sequenceOf(
                "roll shizov",
                "roll solll",
                "item shizov",
                "roll keli",
            )
            eventManager.startHandling()
            actionsSequence.forEach {
                val action = inputParser.parse(it) ?: return@forEach
                eventManager.consumeAction(action)
                if (!action.singleShot) {
                    historyHolder.pushEvent(action)
                }
            }
            historyHolder.save()
            eventManager.stopHandling()
            val restored = historyHolder.load()
            assertEquals(stateHolder.globalStateFlow.value, restored)
        }
    }
}
