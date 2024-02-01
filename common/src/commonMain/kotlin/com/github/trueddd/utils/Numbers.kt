package com.github.trueddd.utils

val Int.isEven: Boolean
    get() = this.rem(2) == 0
