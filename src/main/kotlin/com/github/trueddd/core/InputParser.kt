package com.github.trueddd.core

import com.github.trueddd.core.actions.Action
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.Log
import com.trueddd.github.annotations.ActionGenerator
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class InputParser(
    @Named(ActionGenerator.TAG)
    private val generators: Set<Action.Generator<*>>,
    private val participantProvider: ParticipantProvider,
) {

    companion object {
        const val TAG = "InputParser"
    }

    /**
     * Common pattern that matches all the actions triggered by players.
     * Action syntax: `<username>:<action_key>[:<argument1>..:<argumentN>]`
     */
    private val pattern = Regex("^([a-z]+):(\\d+)(?::([\\w-]+))*\$")

    fun parse(input: String): Action? {
        val parseResult = pattern.matchEntire(input) ?: run {
            Log.error(TAG, "Couldn't parse action from input: $input")
            return null
        }
        val user = parseResult.groupValues.getOrNull(1)?.let { participantProvider[it] } ?: run {
            Log.error(TAG, "Couldn't get `user` from input: $input")
            return null
        }
        val actionId = parseResult.groupValues.getOrNull(2)?.toIntOrNull() ?: run {
            Log.error(TAG, "Couldn't get `actionId` from input: $input")
            return null
        }
        val arguments = parseResult.groupValues.drop(3)
        val generator = generators.firstOrNull { it.actionKey == actionId } ?: run {
            Log.error(TAG, "Couldn't find a generator for input: $input")
            return null
        }
        val action = try {
            generator.generate(user, arguments)
        } catch (error: ActionCreationException) {
            error.printStackTrace()
            return null
        }
        Log.info(TAG, "Input parsed | `$input` -> `$action`")
        return action
    }
}
