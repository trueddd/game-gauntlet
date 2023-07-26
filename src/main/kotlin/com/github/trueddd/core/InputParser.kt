package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
import com.github.trueddd.utils.ActionGeneratorCreationException
import com.github.trueddd.utils.Log
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class InputParser(
    @Named(Action.Generator.SetTag)
    private val generators: Set<Action.Generator<*>>,
) {

    companion object {
        const val TAG = "InputParser"
    }

    fun parse(input: String): Action? {
        val action = generators.firstNotNullOfOrNull { actionGenerator ->
            val match = actionGenerator.inputMatcher.matchEntire(input)
            if (match != null) {
                try {
                    actionGenerator.generate(match)
                } catch (error: ActionGeneratorCreationException) {
                    null
                }
            } else {
                null
            }
        }
        when (action) {
            null -> Log.error(TAG, "Couldn't parse action from input: $input")
            else -> Log.info(TAG, "Parsed action from input: $input")
        }
        return action
    }
}
