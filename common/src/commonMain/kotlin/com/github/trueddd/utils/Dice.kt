package com.github.trueddd.utils

import kotlin.random.Random
import kotlin.random.nextInt

val moveRange = 1 .. 10

val d6Range = 1 .. 6

fun rollDice() = Random.nextInt(d6Range)
