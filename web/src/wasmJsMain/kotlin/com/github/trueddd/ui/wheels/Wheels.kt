package com.github.trueddd.ui.wheels

import androidx.compose.animation.AnimatedContent
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
        val appClient = remember { get<AppClient>() }
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
                text = "Players",
                color = if (wheelType == WheelType.Players) Color.White else Colors.Primary,
                modifier = Modifier
                    .background(if (wheelType == WheelType.Players) Colors.Primary else Color.White)
                    .clickable { wheelType = WheelType.Players }
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
        AnimatedContent(
            targetState = wheelType,
        ) {
            when (it) {
                WheelType.Items -> Wheel(
                    participant = participant,
                    loadItems = { appClient.getItems() },
                    rollItemLambda = { appClient.rollItem()!! },
                    name = { name },
                    description = { description }
                )

                WheelType.Games -> Wheel(
                    participant = participant,
                    loadItems = { appClient.getGames() },
                    rollItemLambda = { appClient.rollGame()!! },
                    name = { name },
                    description = { name },
                )

                WheelType.Players -> Wheel(
                    participant = participant,
                    loadItems = { appClient.getPlayers() },
                    rollItemLambda = { appClient.rollPlayer()!! },
                    name = { displayName },
                    description = { displayName }
                )
            }
        }
    }
}

private suspend fun <T> handleRollItems(
    isRunning: Boolean,
    spinState: SpinState,
    items: List<T>,
    rollItemLambda: suspend () -> T
): SpinState {
    return if (isRunning) {
        spinState.copy(
            running = false,
            initialPosition = 0,
            targetPosition = 0
        )
    } else {
        val item = rollItemLambda()
        spinState.copy(
            running = true,
            initialPosition = spinState.targetPosition,
            targetPosition = items.indexOf(item)
        )
    }
}

@Composable
private fun <T> Wheel(
    participant: Participant,
    loadItems: suspend () -> List<T>,
    rollItemLambda: suspend () -> T,
    name: T.() -> String,
    description: T.() -> String,
) {
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf(emptyList<T>()) }
    LaunchedEffect(Unit) {
        items = loadItems()
    }
    var spinState by remember(items) { mutableStateOf(SpinState.default(itemsCount = items.size)) }
    var isRunning by remember { mutableStateOf(false) }
    var itemPanel by remember { mutableStateOf<T?>(null) }
    LaunchedEffect(spinState) {
        if (spinState.running) {
            isRunning = true
            itemPanel = null
            delay(spinState.duration)
            isRunning = false
        } else {
            isRunning = false
        }
    }
    if (items.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val itemHeight = 52.dp + 8.dp * 2
            val scrollState = rememberLazyListState(
                initialFirstVisibleItemIndex = spinState.numberOfOptionsOnScreen / 2
            )
            val rotate by flatSpinAnimation(spinState) {
                itemPanel = items.getOrNull(spinState.targetPosition.rem(spinState.itemsCount))
            }
            LaunchedEffect(rotate) {
                if (isRunning) {
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
                    .height(itemHeight * spinState.numberOfOptionsOnScreen)
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            ) {
                items(Int.MAX_VALUE) { position ->
                    val item = items[position.rem(items.size)]
                    Text(
                        text = item.name(),
                        fontSize = 46.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(52.dp)
                            .fillMaxWidth()
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Button(
                    shape = RoundedCornerShape(50),
                    onClick = {
                        scope.launch {
                            spinState = handleRollItems(isRunning, spinState, items, rollItemLambda)
                        }
                    },
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text(
                        text = if (isRunning) "Stop" else "Roll"
                    )
                }
                if (itemPanel != null) {
                    Text(
                        text = itemPanel!!.description()
                    )
                }
            }
        }
    }
}
