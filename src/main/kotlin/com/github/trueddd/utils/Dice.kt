package com.github.trueddd.utils

import kotlin.random.Random
import kotlin.random.nextInt

val d6Range = 1 .. 6

fun rollDice() = Random.nextInt(d6Range)
