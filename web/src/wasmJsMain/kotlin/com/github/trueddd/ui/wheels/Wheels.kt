package com.github.trueddd.ui.wheels

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.AppStorage
import com.github.trueddd.core.Command
import com.github.trueddd.core.CommandSender
import com.github.trueddd.data.*
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.ui.res.icons.Star
import com.github.trueddd.ui.widget.DiceAnimation
import com.github.trueddd.ui.widget.DiceD6
import com.github.trueddd.util.applyModifiersDecoration
import com.github.trueddd.util.localized
import com.github.trueddd.util.positionSpinAnimation
import com.github.trueddd.utils.flipCoin
import com.github.trueddd.utils.rollDice
import com.github.trueddd.utils.wheelItems
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wheels(
    player: Participant,
    gameConfig: GameConfig,
    currentPlayerState: PlayerState?,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        val scope = rememberCoroutineScope()
        val commandSender = remember { get<CommandSender>() }
        val appClient = remember { get<AppClient>() }
        val appStorage = remember { get<AppStorage>() }
        var wheelState by remember {
            mutableStateOf(
                WheelState.default(
                    items = emptyList(),
                    type = WheelType.Items
                )
            )
        }
        LaunchedEffect(wheelState.type) {
            val items = when (val type = wheelState.type) {
                is WheelType.Items -> wheelItems
                is WheelType.Games -> appClient.getGames(type.genre)
                is WheelType.Players -> gameConfig.players
                is WheelType.Dice -> DiceValue.All
                is WheelType.Coin -> CoinValue.All
            }
            wheelState = appStorage.getSavedWheelState(items, wheelState.type)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(32.dp)
        ) {
            InputChip(
                selected = wheelState.type == WheelType.Items,
                onClick = { wheelState = stateOnTabChange(WheelType.Items) },
                label = { Text("Предметы") },
                leadingIcon = if (wheelState.type == WheelType.Items) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null,
                trailingIcon = null,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            )
            InputChip(
                selected = wheelState.type == WheelType.Players,
                onClick = { wheelState = stateOnTabChange(WheelType.Players) },
                label = { Text("Игроки") },
                leadingIcon = if (wheelState.type == WheelType.Players) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null,
                trailingIcon = null,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            )
            var gamesExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = gamesExpanded,
                onExpandedChange = { gamesExpanded = !gamesExpanded },
                modifier = Modifier
            ) {
                InputChip(
                    selected = wheelState.type is WheelType.Games,
                    onClick = {},
                    label = {
                        Text(text = wheelState.type.let {
                            if (it is WheelType.Games) "Игры - ${it.genre.localized}" else "Игры"
                        })
                    },
                    leadingIcon = if (wheelState.type is WheelType.Games) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null,
                    trailingIcon = null,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = gamesExpanded,
                    onDismissRequest = { gamesExpanded = false },
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Game.Genre.entries.forEach {
                        DropdownMenuItem(
                            text = { Row {
                                val current = currentPlayerState?.genreOfCurrentPosition(gameConfig) == it
                                if (current) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        modifier = Modifier
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(24.dp))
                                }
                                Text(text = it.localized)
                                Spacer(modifier = Modifier.width(24.dp))
                            } },
                            onClick = {
                                wheelState = stateOnTabChange(WheelType.Games(it))
                                gamesExpanded = false
                            },
                        )
                    }
                }
            }
            InputChip(
                selected = wheelState.type == WheelType.Dice,
                onClick = { wheelState = stateOnTabChange(WheelType.Dice) },
                label = { Text("Кубик") },
                leadingIcon = if (wheelState.type == WheelType.Dice) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null,
                trailingIcon = null,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            )
            InputChip(
                selected = wheelState.type == WheelType.Coin,
                onClick = { wheelState = stateOnTabChange(WheelType.Coin) },
                label = { Text("Монетка") },
                leadingIcon = if (wheelState.type == WheelType.Coin) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null,
                trailingIcon = null,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                shape = RoundedCornerShape(50),
                enabled = !wheelState.running,
                onClick = {
                    scope.launch {
                        val rollLambda = suspend {
                            when (val type = wheelState.type) {
                                is WheelType.Items -> appClient.rollItem()!!
                                is WheelType.Games -> appClient.rollGame(type.genre)!!
                                is WheelType.Players -> appClient.rollPlayer()!!
                                is WheelType.Dice -> throw IllegalStateException()
                                is WheelType.Coin -> throw IllegalStateException()
                            }
                        }
                        wheelState = when (wheelState.type) {
                            WheelType.Dice -> {
                                val value = rollDice()
                                wheelState.copy(
                                    rolledItem = DiceValue(value),
                                    running = true,
                                    targetPosition = value,
                                    initialPosition = wheelState.targetPosition
                                )
                            }
                            WheelType.Coin -> {
                                val value = flipCoin()
                                wheelState.copy(
                                    rolledItem = CoinValue(value),
                                    running = true,
                                    targetPosition = if (value) 1 else 0,
                                    initialPosition = wheelState.targetPosition
                                )
                            }
                            else -> handleRollItems(wheelState, rollLambda)
                        }
                    }
                },
                modifier = Modifier
                    .pointerHoverIcon(if (wheelState.running) PointerIcon.Default else PointerIcon.Hand)
            ) {
                Text(text = "Крутить")
            }
            if (wheelState.type.let { it is WheelType.Items || it is WheelType.Games } && wheelState.rolledItem != null) {
                OutlinedButton(
                    shape = RoundedCornerShape(50),
                    onClick = {
                        when (val rollable = wheelState.rolledItem) {
                            is WheelItem -> commandSender.sendCommand(
                                Command.Action.itemReceive(player, rollable.id)
                            )
                            is Game -> commandSender.sendCommand(
                                Command.Action.gameRoll(player, rollable.id)
                            )
                        }
                        wheelState = wheelState.copy(rolledItem = null)
                    },
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text(
                        text = when (wheelState.type) {
                            is WheelType.Items -> "Принять"
                            is WheelType.Games -> "Сделать текущей"
                            else -> "Apply"
                        }
                    )
                }
                if (wheelState.type is WheelType.Games && currentPlayerState?.canSetNextGame == true) {
                    OutlinedButton(
                        shape = RoundedCornerShape(50),
                        onClick = {
                            (wheelState.rolledItem as? Game)?.let {
                                commandSender.sendCommand(Command.Action.gameSet(player, it.id))
                                wheelState = wheelState.copy(rolledItem = null)
                            }
                        },
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text(text = "Сделать следующей")
                    }
                }
            }
        }
        AnimatedContent(
            targetState = wheelState.items,
        ) {
            when (wheelState.type) {
                WheelType.Dice -> DiceBlock(wheelState) {
                    wheelState = wheelState.copy(running = false)
                    appStorage.saveWheelItemsState(wheelState)
                }
                WheelType.Coin -> CoinBlock(wheelState) {
                    wheelState = wheelState.copy(running = false)
                    appStorage.saveWheelItemsState(wheelState)
                }
                else -> Wheel(
                    wheelState = wheelState,
                    onRollFinished = {
                        wheelState = wheelState.copy(
                            running = false,
                            rolledItem = wheelState.items.getOrNull(
                                wheelState.targetPosition.rem(wheelState.items.size)
                            )
                        )
                        appStorage.saveWheelItemsState(wheelState)
                    },
                )
            }
        }
    }
}

@Composable
private fun CoinBlock(
    wheelState: WheelState,
    onFlipFinished: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        ) {
            if (wheelState.rolledItem as? CoinValue != null) {
                Coin(
                    coinValue = wheelState.rolledItem,
                    shouldAnimate = wheelState.running,
                    onFlipFinished = onFlipFinished,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(96.dp)
                )
            }
        }
    }
}

@Composable
private fun DiceBlock(
    wheelState: WheelState,
    onRollFinished: () -> Unit,
) {
    var diceAnimation by remember { mutableStateOf(DiceAnimation(
        randomChangesAmount = 0,
        swingEnabled = false,
        dotsMoveEnabled = true,
        duration = 300
    )) }
    LaunchedEffect(wheelState.rolledItem, wheelState.running) {
        if (wheelState.rolledItem == null) return@LaunchedEffect
        if (!wheelState.running) return@LaunchedEffect
        diceAnimation = DiceAnimation(
            randomChangesAmount = 20,
            swingEnabled = true,
            dotsMoveEnabled = true,
            duration = 500
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        ) {
            DiceD6(
                value = (wheelState.rolledItem as? DiceValue)?.value ?: 1,
                diceAnimation = diceAnimation,
                dotSize = 12.dp,
                borderSize = 8.dp,
                onRollFinished = onRollFinished,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(96.dp)
            )
        }
    }
}

private fun stateOnTabChange(type: WheelType): WheelState {
    return WheelState.default(
        items = emptyList(),
        type = type,
    ).copy(rolledItem = null)
}

private suspend fun <T : Rollable> handleRollItems(
    wheelState: WheelState,
    rollItemLambda: suspend () -> T
): WheelState {
    val item = rollItemLambda()
    return wheelState.copy(
        running = true,
        initialPosition = wheelState.targetPosition,
        targetPosition = wheelState.items.indexOf(item),
        rolledItem = null,
    )
}

@Composable
private fun Wheel(
    wheelState: WheelState,
    onRollFinished: () -> Unit,
) {
    var rolledBefore by remember { mutableStateOf(false) }
    LaunchedEffect(wheelState.running) {
        if (wheelState.running) {
            rolledBefore = true
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(vertical = 32.dp)
    ) {
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
                val rotate by positionSpinAnimation(wheelState, onRollFinished)
                LaunchedEffect(rotate) {
                    scrollState.animateScrollToItem(rotate)
                    if (!rolledBefore && !wheelState.running) {
                        val scrollPosition = wheelState.targetPosition
                            .plus(wheelState.items.size)
                            .minus(wheelState.numberOfOptionsOnScreen / 2)
                            .rem(wheelState.items.size)
                        scrollState.scrollToItem(scrollPosition)
                    }
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
                                            .border(4.dp, Color(item.color), CircleShape)
                                            .let {
                                                if (wheelState.rolledItem == item) {
                                                    it.padding(8.dp).background(Color(item.color), CircleShape)
                                                } else {
                                                    it
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
                    if (wheelState.rolledItem != null) {
                        Text(
                            text = if (wheelState.type == WheelType.Items) {
                                wheelState.rolledItem.description.applyModifiersDecoration()
                            } else {
                                AnnotatedString(wheelState.rolledItem.description)
                            },
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
