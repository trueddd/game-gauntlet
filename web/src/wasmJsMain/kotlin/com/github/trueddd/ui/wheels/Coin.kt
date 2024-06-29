package com.github.trueddd.ui.wheels

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.util.dashedBorder
import kotlin.math.absoluteValue

@Immutable
private data class CoinAnimationState(
    val value: Boolean?,
    val rotate: Float,
)

private const val COIN_FLIPS = 10

@Composable
fun Coin(
    coinValue: CoinValue,
    shouldAnimate: Boolean,
    modifier: Modifier = Modifier,
    onFlipFinish: () -> Unit = {},
) {
    val color = MaterialTheme.colorScheme.primary
    var animationState by remember { mutableStateOf(CoinAnimationState(null, 0f)) }
    val latestOnFlipFinish by rememberUpdatedState(onFlipFinish)

    LaunchedEffect(coinValue, shouldAnimate) {
        val repeatTimes = when {
            !shouldAnimate -> 1
            coinValue.value == animationState.value -> COIN_FLIPS
            else -> COIN_FLIPS + 1
        }
        repeat(repeatTimes) {
            val newValue = animationState.value?.not() ?: coinValue.value
            animate(
                initialValue = if (animationState.value != null) -1f else 0f,
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            ) { value, _ ->
                animationState = animationState.copy(rotate = value, value = newValue)
            }
        }
        latestOnFlipFinish()
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer(
                scaleY = animationState.rotate.absoluteValue,
            )
            .border(4.dp, color, CircleShape)
            .padding(6.dp)
            .dashedBorder(color, CircleShape, strokeWidth = 2.dp, dashWidth = 4.dp, gapWidth = 6.dp)
    ) {
        animationState.value?.let { isHeads ->
            if ((isHeads && animationState.rotate >= 0f) || (!isHeads && animationState.rotate < 0f)) {
                Text(
                    text = "Р",
                    fontSize = 36.sp,
                    color = color,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = "О",
                    fontSize = 36.sp,
                    color = color,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}
