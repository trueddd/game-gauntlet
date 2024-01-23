package com.github.trueddd.utils

import kotlinx.datetime.Clock

object Timer {

    fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
