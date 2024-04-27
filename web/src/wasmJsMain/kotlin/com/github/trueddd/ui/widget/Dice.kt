package com.github.trueddd.ui.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

enum class DiceType(val base: Int) {
//    D4,
    D6(6),
//    D8,
//    D10,
//    D12,
//    D20
}

@Composable
fun Dice(
    value: Int,
    type: DiceType = DiceType.D6,
) {
    when (type) {
        DiceType.D6 -> DiceD6(value)
    }
}

private fun DrawScope.drawDot(offset: Offset, size: Size, color: Color) {
    drawOval(
        color = color,
        topLeft = offset - Offset(size.width / 2, size.height / 2),
        size = size,
    )
}

@Stable
data class Dot(
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

@Composable
fun DiceD6(
    value: Int,
    diceSize: Dp = 32.dp,
    dotSize: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary
    val dotSizePx = with(LocalDensity.current) { Size(dotSize.toPx(), dotSize.toPx()) }
    var previousValue by remember { mutableStateOf(0) }
    var animationState by remember { mutableStateOf(0 .. value) }
    var shift by remember { mutableStateOf(-1f) }
    LaunchedEffect(value) {
        if (value != previousValue) {
            animationState = previousValue .. value
            previousValue = value
            animate(
                initialValue = -1f,
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            ) { value, _ ->
                shift = value
            }
        }
    }
    val dots = remember(animationState, shift) {
        when {
            shift < 0f -> createDots(animationState.first, shift.absoluteValue)
            shift == 0f -> listOf(Dot(0.5f, 0.5f))
            shift > 0f -> createDots(animationState.last, shift.absoluteValue)
            else -> emptyList()
        }
    }
    Canvas(
        modifier = modifier
            .border(2.dp, color, RoundedCornerShape(4.dp))
            .size(diceSize)
    ) {
        for (dot in dots) {
            drawDot(Offset(dot.x * size.width, dot.y * size.height), dotSizePx, color)
        }
    }
}
