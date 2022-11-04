package com.github.trueddd.core

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.generator.ActionGenerator
import com.github.trueddd.core.generator.GameDropGenerator
import com.github.trueddd.core.generator.ItemReceiveGenerator
import com.github.trueddd.core.generator.MoveForwardGenerator
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class InputParser(
    stateHolder: StateHolder,
    itemRoller: ItemRoller,
) {

    // TODO: provide list of available generators using annotations
    private val generators = listOf<ActionGenerator<*>>(
        GameDropGenerator(),
        MoveForwardGenerator(stateHolder),
        ItemReceiveGenerator(itemRoller),
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
