package com.github.trueddd.core.generator

import com.github.trueddd.core.actions.Action

/**
 * Action generator is a component that creates actions from entered command by any of participants.
 *
 */
interface ActionGenerator<A : Action> {

    companion object {
        const val TAG = "ActionGenerator"
    }

    val inputMatcher: Regex

    fun generate(matchResult: MatchResult): A
}
