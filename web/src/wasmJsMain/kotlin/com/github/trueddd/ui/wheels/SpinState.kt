package com.github.trueddd.ui.wheels

import kotlin.time.Duration.Companion.seconds

data class SpinState(
    val running: Boolean,
    val duration: Long,
    val initialPosition: Int,
    val targetPosition: Int,
    val itemsCount: Int,
    val numberOfOptionsOnScreen: Int,
) {

    companion object {
        fun default(itemsCount: Int) = SpinState(
            running = false,
            duration = 20.seconds.inWholeMilliseconds,
            initialPosition = 0,
            targetPosition = 0,
            itemsCount = itemsCount,
            numberOfOptionsOnScreen = 7,
        )
    }
}
