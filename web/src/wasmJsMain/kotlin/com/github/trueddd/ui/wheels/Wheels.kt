package com.github.trueddd.ui.wheels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.core.AppClient
import com.github.trueddd.data.Participant
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.flatSpinAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Wheels(
    participant: Participant,
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
            WheelType.Items -> ItemsWheel(participant = participant)
            WheelType.Games -> {}
            WheelType.Players -> {}
        }
    }
}

private suspend fun handleRollItems(
    spinState: SpinState,
    items: List<WheelItem>,
    appClient: AppClient
): SpinState {
    return if (spinState.enabled) {
        spinState.copy(enabled = false, targetPosition = 0)
    } else {
        val item = appClient.rollItem()!!
        spinState.copy(enabled = true, targetPosition = items.indexOf(item) + items.size * 3)
    }
}

@Composable
private fun ItemsWheel(
    participant: Participant,
) {
    val appClient = remember { get<AppClient>() }
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf(emptyList<WheelItem>()) }
    LaunchedEffect(Unit) {
        items = appClient.getItems()
    }
    var spinState by remember(items) { mutableStateOf(SpinState.default(itemsCount = items.size)) }
    var isRunning by remember(spinState.spinTime) { mutableStateOf(spinState.enabled) }
    LaunchedEffect(spinState.spinTime) {
        if (spinState.enabled) {
            delay(spinState.duration)
            isRunning = false
        }
    }
    if (items.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val itemHeight = 52.dp + 8.dp * 2
            val rotate by flatSpinAnimation(spinState, spinState.duration.toInt()) {
            }
            val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = 3)
            LaunchedEffect(rotate) {
                if (spinState.enabled) {
                    scrollState.animateScrollToItem(rotate)
                } else {
                    scrollState.scrollToItem(rotate)
                }
            }
            LazyColumn(
                userScrollEnabled = false,
                state = scrollState,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(itemHeight * 7)
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            ) {
                items(Int.MAX_VALUE) { position ->
                    val item = items[position.rem(items.size)]
                    Text(
                        text = item.name,
                        fontSize = 48.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(52.dp)
                            .fillMaxWidth()
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Button(
                    shape = RoundedCornerShape(50),
                    onClick = {
                        scope.launch {
                            spinState = handleRollItems(spinState, items, appClient)
                        }
                    },
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text(
                        text = if (isRunning) "Stop" else "Roll"
                    )
                }
            }
        }
    }
}
