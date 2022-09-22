package com.github.trueddd.core.generator

import com.github.trueddd.core.Action

interface ActionGenerator<A : Action> {

    val inputMatcher: Regex

    fun generate(input: String): A
}
