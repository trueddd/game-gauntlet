package com.github.trueddd.core

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.generator.ActionGenerator
import org.koin.core.annotation.Single

@Single
class InputParser(
    private val generators: Set<ActionGenerator<*>>,
) {

    fun parse(input: String): Action? {
        return generators.firstNotNullOfOrNull { actionGenerator ->
            val match = actionGenerator.inputMatcher.matchEntire(input)
            if (match != null) {
                actionGenerator.generate(input)
            } else {
                null
            }
        }
    }
}
