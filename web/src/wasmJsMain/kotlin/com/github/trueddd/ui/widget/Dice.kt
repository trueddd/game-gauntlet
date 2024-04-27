package com.github.trueddd.ui.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

private fun DrawScope.drawDot(offset: Offset, size: Size, color: Color) {
    drawOval(
        color = color,
        topLeft = offset - Offset(size.width / 2, size.height / 2),
        size = size,
    )
}

@Immutable
private data class Dot(
    val x: Float,
    val y: Float,
)

private fun Float.lerpToCenter(distanceFromCenter: Float): Float {
    return lerp(0.5f, this, distanceFromCenter)
}

private fun createDots(value: Int, distance: Float) = buildList {
    if (value.rem(2) == 1) {
        add(Dot(0.5f, 0.5f))
    }
    if (value > 1) {
        add(Dot(0.25f.lerpToCenter(distance), 0.75f.lerpToCenter(distance)))
        add(Dot(0.75f.lerpToCenter(distance), 0.25f.lerpToCenter(distance)))
    }
    if (value > 3) {
        add(Dot(0.25f.lerpToCenter(distance), 0.25f.lerpToCenter(distance)))
        add(Dot(0.75f.lerpToCenter(distance), 0.75f.lerpToCenter(distance)))
    }
    if (value == 6) {
        add(Dot(0.25f.lerpToCenter(distance), 0.5f.lerpToCenter(distance)))
        add(Dot(0.75f.lerpToCenter(distance), 0.5f.lerpToCenter(distance)))
    }
}

@Immutable
private data class DiceAnimationState(
    val shift: Float,
    val from: Int,
    val to: Int,
    val swingLeft: Boolean,
) {
    constructor(value: Int) : this(shift = -1f, from = 0, to = value, swingLeft = true)
}

@Immutable
data class DiceAnimation(
    val randomChangesAmount: Int = 0,
    val duration: Int = 400,
    val swingEnabled: Boolean = false,
    val dotsMoveEnabled: Boolean = true,
)

private fun rollNotEqual(previous: Int, next: Int?): Int {
    return (1..6).filter { it != previous && it != next }.random()
}

@Composable
fun DiceD6(
    value: Int,
    diceAnimation: DiceAnimation = DiceAnimation(),
    dotSize: Dp = 4.dp,
    borderSize: Dp = 2.dp,
    modifier: Modifier = Modifier,
    onRollFinished: () -> Unit = {},
) {
    val color = MaterialTheme.colorScheme.primary
    val dotSizePx = with(LocalDensity.current) { Size(dotSize.toPx(), dotSize.toPx()) }
    var animationState by remember { mutableStateOf(DiceAnimationState(value)) }
    LaunchedEffect(value, diceAnimation.randomChangesAmount) {
        repeat(diceAnimation.randomChangesAmount + 1) { index ->
            val next = value.takeIf { index == diceAnimation.randomChangesAmount - 1 }
            val newValue = when (index) {
                diceAnimation.randomChangesAmount -> value
                else -> rollNotEqual(animationState.to, next)
            }
            animationState = when {
                animationState.shift < 0f -> animationState.copy(
                    shift = animationState.shift,
                    to = newValue,
                )
                else -> DiceAnimationState(
                    shift = -animationState.shift,
                    from = animationState.to,
                    to = newValue,
                    swingLeft = !animationState.swingLeft,
                )
            }
            animate(
                initialValue = animationState.shift,
                targetValue = 1f,
                animationSpec = tween(durationMillis = diceAnimation.duration, easing = LinearEasing)
            ) { value, _ ->
                animationState = animationState.copy(shift = value)
            }
        }
        onRollFinished()
    }
    val dots = remember(animationState, diceAnimation.dotsMoveEnabled) {
        val shift = if (diceAnimation.dotsMoveEnabled) animationState.shift.absoluteValue else 1f
        when {
            animationState.shift < 0f -> createDots(animationState.from, shift)
            animationState.shift == 0f -> listOf(Dot(0.5f, 0.5f))
            animationState.shift > 0f -> createDots(animationState.to, shift)
            else -> emptyList()
        }
    }
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer(
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (animationState.swingLeft) 0.1f else 0.9f,
                    pivotFractionY = 0.9f
                ),
                rotationZ = when (diceAnimation.swingEnabled) {
                    true -> lerp(45f, 0f, animationState.shift.absoluteValue)
                        .let { if (animationState.swingLeft) -it else it }
                    else -> 0f
                }
            )
            .border(borderSize, color, RoundedCornerShape(borderSize * 2))
    ) {
        for (dot in dots) {
            drawDot(Offset(dot.x * size.width, dot.y * size.height), dotSizePx, color)
        }
    }
}
