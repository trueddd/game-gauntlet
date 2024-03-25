package com.github.trueddd.ui.wheels

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.Command
import com.github.trueddd.data.Participant
import com.github.trueddd.data.Rollable
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.util.positionSpinAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wheels(
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        val scope = rememberCoroutineScope()
        val appClient = remember { get<AppClient>() }
        var wheelState by remember { mutableStateOf(
            WheelState.default(
                items = emptyList(),
                type = WheelType.Items
            )
        ) }
        LaunchedEffect(wheelState.type) {
            val items = when (wheelState.type) {
                WheelType.Items -> appClient.getItems()
                WheelType.Games -> appClient.getGames()
                WheelType.Players -> appClient.getPlayers()
            }
            wheelState = wheelState.copy(items = items)
        }
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(32.dp)
        ) {
            SegmentedButton(
                selected = wheelState.type == WheelType.Items,
                onClick = { wheelState = wheelState.copy(type = WheelType.Items) },
                label = { Text("Предметы") },
                shape = RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50),
            )
            SegmentedButton(
                selected = wheelState.type == WheelType.Players,
                onClick = { wheelState = wheelState.copy(type = WheelType.Players) },
                label = { Text("Игроки") },
                shape = RectangleShape,
            )
            SegmentedButton(
                selected = wheelState.type == WheelType.Games,
                onClick = { wheelState = wheelState.copy(type = WheelType.Games) },
                label = { Text("Игры") },
                shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50),
            )
        }
        AnimatedContent(
            targetState = wheelState.type,
        ) { type ->
            when (type) {
                WheelType.Items -> Wheel(
                    wheelState = wheelState,
                    onApplyClicked = {
                        if (it !is WheelItem) return@Wheel
                        appClient.sendCommand(Command.Action.itemReceive(participant, it.id))
                    },
                    onRollClicked = {
                        scope.launch {
                            wheelState = handleRollItems(wheelState) { appClient.rollItem()!! }
                        }
                    },
                    onRollFinished = { wheelState = wheelState.copy(running = false) },
                )

                WheelType.Games -> Wheel(
                    wheelState = wheelState,
                    onRollClicked = {
                        scope.launch {
                            wheelState = handleRollItems(wheelState) { appClient.rollGame()!! }
                        }
                    },
                    onRollFinished = { wheelState = wheelState.copy(running = false) },
                )

                WheelType.Players -> Wheel(
                    wheelState = wheelState,
                    onRollClicked = {
                        scope.launch {
                            wheelState = handleRollItems(wheelState) { appClient.rollPlayer()!! }
                        }
                    },
                    onRollFinished = { wheelState = wheelState.copy(running = false) },
                )
            }
        }
    }
}

private suspend fun <T : Rollable> handleRollItems(
    wheelState: WheelState,
    rollItemLambda: suspend () -> T
): WheelState {
    val item = rollItemLambda()
    return wheelState.copy(
        running = true,
        initialPosition = wheelState.targetPosition,
        targetPosition = wheelState.items.indexOf(item)
    )
}

@Composable
private fun Wheel(
    wheelState: WheelState,
    onRollClicked: () -> Unit,
    onApplyClicked: (Rollable) -> Unit = {},
    onRollFinished: () -> Unit,
) {
    var isRunning by remember { mutableStateOf(false) }
    var rolledItem by remember { mutableStateOf<Rollable?>(null) }
    LaunchedEffect(wheelState) {
        if (wheelState.running) {
            println(wheelState.toString())
            isRunning = true
            rolledItem = null
            delay(wheelState.duration)
            isRunning = false
        } else {
            isRunning = false
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(vertical = 32.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                shape = RoundedCornerShape(50),
                enabled = !isRunning,
                onClick = onRollClicked,
                modifier = Modifier
                    .pointerHoverIcon(if (isRunning) PointerIcon.Default else PointerIcon.Hand)
            ) {
                Text(text = "Крутить")
            }
            if (wheelState.type != WheelType.Players && rolledItem != null) {
                OutlinedButton(
                    shape = RoundedCornerShape(50),
                    onClick = { rolledItem?.let(onApplyClicked) },
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text(
                        text = when (wheelState.type) {
                            WheelType.Items -> "Принять"
                            WheelType.Games -> "Сделать текущей"
                            WheelType.Players -> "Apply"
                        }
                    )
                }
            }
        }
        if (wheelState.items.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                modifier = Modifier
                    .padding(horizontal = 64.dp)
                    .fillMaxSize()
            ) {
                val itemHeight = 52.dp
                val itemPadding = 12.dp
                val itemShift = 64.dp
                val wholeItemHeight = itemHeight + itemPadding * 2
                val wholeItemHeightPx = with(LocalDensity.current) { wholeItemHeight.toPx() }
                val scrollState = rememberLazyListState()
                val rotate by positionSpinAnimation(wheelState) {
                    rolledItem = wheelState.items.getOrNull(wheelState.targetPosition.rem(wheelState.items.size))
                    isRunning = false
                    onRollFinished()
                }
                LaunchedEffect(rotate) {
                    scrollState.animateScrollToItem(rotate)
                }
                LazyColumn(
                    userScrollEnabled = false,
                    state = scrollState,
                    modifier = Modifier
                        .height(wholeItemHeight * wheelState.numberOfOptionsOnScreen)
                        .weight(3f)
                        .align(Alignment.CenterVertically)
                ) {
                    items(Int.MAX_VALUE) { position ->
                        val item = wheelState.items[position.rem(wheelState.items.size)]
                        val fraction = remember {
                            derivedStateOf {
                                scrollState.layoutInfo.visibleItemsInfo
                                    .firstOrNull { it.index == position }
                                    ?.offset?.plus(wholeItemHeightPx / 2)
                                    ?.div(wholeItemHeightPx * wheelState.numberOfOptionsOnScreen)
                                    ?.minus(0.5f)?.times(2)
                                    ?.let { 1f - it * it }?.coerceIn(0f..1f)
                                    ?: 0f
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(percent = 50),
                            modifier = Modifier
                                .padding(
                                    top = itemPadding,
                                    bottom = itemPadding,
                                    start = itemShift * fraction.value,
                                    end = itemShift * (1 - fraction.value)
                                )
                                .height(itemHeight)
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                if (wheelState.type == WheelType.Items) {
                                    Spacer(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .run {
                                                if (rolledItem == item) {
                                                    background(Color(item.color), CircleShape)
                                                } else {
                                                    border(4.dp, Color(item.color), CircleShape)
                                                }
                                            }
                                    )
                                }
                                Text(
                                    text = item.name,
                                    fontSize = 36.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f)
                        .align(Alignment.CenterVertically)
                ) {
                    if (rolledItem != null) {
                        Text(
                            text = rolledItem!!.description,
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                        )
                    }
                }
            }
        }
    }
}
