package com.github.trueddd.utils

import kotlin.random.Random
import kotlin.random.nextInt

private const val MIN = 1
private const val MAX = 10

val d6Range = 1 .. 6

fun rollDice() = Random.nextInt(d6Range)

fun coerceDiceValue(initial: Int) = initial.coerceAtLeast(MIN).coerceAtMost(MAX)
