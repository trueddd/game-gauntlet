package com.github.trueddd.core

import com.github.trueddd.actions.Action

interface InputParser {

    fun parse(input: String): Action?
}
