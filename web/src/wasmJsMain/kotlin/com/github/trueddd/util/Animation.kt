package com.github.trueddd.util

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.github.trueddd.ui.wheels.WheelState

@Composable
fun positionSpinAnimation(
    state: WheelState,
    onFinish: () -> Unit = {},
): State<Int> {
    var playTime by remember { mutableLongStateOf(0L) }
    val rotate = remember { mutableIntStateOf(0) }
    val latestOnFinish by rememberUpdatedState(onFinish)

    val anim = remember(state) {
        if (!state.running) return@remember null
        val startPosition = when {
            rotate.value != 0 -> rotate.value // subsequent roll
            state.initialPosition == 0 -> state.items.size - state.numberOfOptionsOnScreen / 2 // first roll, no saved
            else -> state.initialPosition - state.numberOfOptionsOnScreen / 2 // first roll, saved is present
        }
        TargetBasedAnimation(
            animationSpec = tween(state.duration.toInt(), easing = FastOutSlowInEasing),
            typeConverter = Int.VectorConverter,
            initialValue = startPosition,
            targetValue = with(state) {
                val delta = (targetPosition - initialPosition).let { if (it < 0) it + items.size else it }
                startPosition + delta + 2 * items.size
            },
        )
    }

    LaunchedEffect(anim) {
        if (anim == null) return@LaunchedEffect
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            rotate.value = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))
        latestOnFinish()
    }
    return rotate
}

@Suppress("unused") // TODO: use pixel-based animation for smoother scroll
@Composable
fun offsetSpinAnimation(
    state: WheelState,
    positionOffsetPx: Float,
    onFinish: () -> Unit = {},
): State<Float> {
    var playTime by remember { mutableLongStateOf(0L) }
    val targetPixelsOffset = remember { mutableFloatStateOf(0f) }
    val pixelsShift = remember { mutableFloatStateOf(0f) }
    val latestOnFinish by rememberUpdatedState(onFinish)

    val anim = remember(state) {
        if (!state.running) return@remember null
        TargetBasedAnimation(
            animationSpec = tween(state.duration.toInt(), easing = EaseInOutSine),
            typeConverter = Float.VectorConverter,
            initialValue = 0f,
            targetValue = with(state) {
                val delta = (targetPosition - initialPosition).let { if (it < 0) it + items.size else it }
                val firstShift = (numberOfOptionsOnScreen / 2).takeIf { targetPixelsOffset.value == 0f } ?: 0
                (delta - firstShift + items.size) * positionOffsetPx
            },
        )
    }

    LaunchedEffect(anim) {
        if (anim == null) return@LaunchedEffect
        val startTime = withFrameNanos { it }
        do {
            playTime = withFrameNanos { it } - startTime
            val newTarget = anim.getValueFromNanos(playTime)
            pixelsShift.value = newTarget - targetPixelsOffset.value
            targetPixelsOffset.value = newTarget
        } while (!anim.isFinishedFromNanos(playTime))
        latestOnFinish()
    }
    return pixelsShift
}
