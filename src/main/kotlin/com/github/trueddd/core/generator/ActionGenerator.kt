package com.github.trueddd.core.generator

import com.github.trueddd.core.events.Action

interface ActionGenerator<A : Action> {

    companion object {
        const val TAG = "ActionGenerator"
    }

    val inputMatcher: Regex

    fun generate(input: String): A
}
