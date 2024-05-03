package com.github.trueddd.ui.wheels

import androidx.compose.runtime.Immutable
import com.github.trueddd.data.Rollable
import kotlin.time.Duration.Companion.seconds

@Immutable
data class WheelState(
    val type: WheelType,
    val items: List<Rollable>,
    val running: Boolean,
    val duration: Long,
    val initialPosition: Int,
    val targetPosition: Int,
    val numberOfOptionsOnScreen: Int,
    val rolledItem: Rollable?,
) {

    companion object {
        fun default(items: List<Rollable>, type: WheelType) = WheelState(
            type = type,
            items = items,
            running = false,
            duration = 20.seconds.inWholeMilliseconds,
            initialPosition = 0,
            targetPosition = 0,
            numberOfOptionsOnScreen = 7,
            rolledItem = null,
        )
    }
}
