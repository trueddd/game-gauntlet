package com.github.trueddd.ui.wheels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.flatSpinAnimation
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Composable
fun Wheels(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(top = 16.dp)
    ) {
        var wheelType by remember { mutableStateOf(WheelType.Items) }
        Row {
            Text(
                text = "Items",
                color = if (wheelType == WheelType.Items) Color.White else Colors.Primary,
                modifier = Modifier
                    .background(if (wheelType == WheelType.Items) Colors.Primary else Color.White)
                    .clickable { wheelType = WheelType.Items }
                    .padding(4.dp)
            )
            Text(
                text = "Games",
                color = if (wheelType == WheelType.Games) Color.White else Colors.Primary,
                modifier = Modifier
                    .background(if (wheelType == WheelType.Games) Colors.Primary else Color.White)
                    .clickable { wheelType = WheelType.Games }
                    .padding(4.dp)
            )
        }
        when (wheelType) {
            WheelType.Items -> ItemsWheel()
            WheelType.Games -> {}
            WheelType.Players -> {}
        }
    }
}

data class SpinState(
    val enabled: Boolean,
    val duration: Long,
    val targetPosition: Int,
) {

    companion object {
        fun default() = SpinState(
            enabled = false,
            duration = 20.seconds.inWholeMilliseconds,
            targetPosition = 0,
        )
    }

    val spinTime = Clock.System.now().toEpochMilliseconds()

    private fun getRandomSpinDelta(range: Int): Int {
        return range * 3 + Random.nextInt(range)
    }

    fun reset(targetPosition: Int = 3) = copy(enabled = false, targetPosition = targetPosition)
    fun spin(range: Int) = copy(enabled = true, targetPosition = getRandomSpinDelta(range))
}

@Composable
private fun ItemsWheel() {
    val items = remember { (1..50).toList() }
    var spinState by remember { mutableStateOf(SpinState.default()) }
    var isRunning by remember(spinState.spinTime) { mutableStateOf(spinState.enabled) }
    LaunchedEffect(spinState.spinTime) {
        if (spinState.enabled) {
            delay(spinState.duration)
            isRunning = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val rotate by flatSpinAnimation(spinState, spinState.duration.toInt()) {
        }
        val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 3)
        LaunchedEffect(rotate) {
            scrollState.scrollToItem(rotate)
        }
        Box(
            modifier = Modifier,
        ) {
            LazyColumn(
                userScrollEnabled = false,
                state = scrollState,
                modifier = Modifier
                    .height((52 * 7).dp)
            ) {
                items(Int.MAX_VALUE) { position ->
                    val item = items[position.rem(items.size)]
                    Text(
                        text = "$item",
                        fontSize = 48.sp,
                        modifier = Modifier
                            .height(52.dp)
                    )
                }
            }
        }
        Button(
            onClick = { spinState = if (isRunning) spinState.reset(rotate) else spinState.spin(items.size) },
        ) {
            Text(
                text = "Roll"
            )
        }
    }
}
