package com.github.trueddd.core.generator

import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class InputParser(
    globalStateFlow: StateFlow<GlobalState>,
) {

    private val generators = listOf<ActionGenerator<*>>(
        GameDropGenerator(),
        MoveForwardGenerator(globalStateFlow),
    )

    suspend fun parse(input: String): Action? {
        return withContext(Dispatchers.Default) {
            generators.firstNotNullOfOrNull { actionGenerator ->
                val match = actionGenerator.inputMatcher.matchEntire(input)
                if (match != null) {
                    actionGenerator.generate(input)
                } else {
                    null
                }
            }
        }
    }
}
