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
    spinDuration: Int,
    onFinished: () -> Unit = {},
): State<Int> {
    val anim = remember(state.spinTime) {
        TargetBasedAnimation(
            animationSpec = tween(spinDuration, easing = FastOutSlowInEasing),
            typeConverter = Int.VectorConverter,
            initialValue = 0,
            targetValue = state.targetPosition,
        )
    }
    var playTime by remember { mutableStateOf(0L) }
    val rotate = remember { mutableStateOf(0) }
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
