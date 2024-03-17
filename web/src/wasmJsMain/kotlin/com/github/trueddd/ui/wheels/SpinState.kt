package com.github.trueddd.ui.wheels

import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

data class SpinState(
    val enabled: Boolean,
    val duration: Long,
    val targetPosition: Int,
    val itemsCount: Int,
) {

    companion object {
        fun default(itemsCount: Int) = SpinState(
            enabled = false,
            duration = 20.seconds.inWholeMilliseconds,
            targetPosition = 0,
            itemsCount = itemsCount,
        )
    }

    val spinTime = Clock.System.now().toEpochMilliseconds()
}
