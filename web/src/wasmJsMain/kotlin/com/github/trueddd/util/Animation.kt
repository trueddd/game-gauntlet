package com.github.trueddd.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import com.github.trueddd.ui.wheels.SpinState

@Composable
fun flatSpinAnimation(
    state: SpinState,
    onFinished: () -> Unit = {},
): State<Int> {
    var playTime by remember { mutableStateOf(0L) }
    val rotate = remember { mutableStateOf(0) }
    val anim = remember(state) {
        TargetBasedAnimation(
            animationSpec = tween(state.duration.toInt(), easing = FastOutSlowInEasing),
            typeConverter = Int.VectorConverter,
            initialValue = rotate.value,
            targetValue = with(state) {
                if (!running) return@with 0
                val delta = (targetPosition - initialPosition).let { if (it < 0) it + itemsCount else it }
                val firstShift = (numberOfOptionsOnScreen / 2).takeIf { rotate.value == 0 } ?: 0
                rotate.value + delta - firstShift + 2 * itemsCount
            },
        )
    }
    LaunchedEffect(anim) {
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            rotate.value = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))
        onFinished()
    }
    return rotate
}
