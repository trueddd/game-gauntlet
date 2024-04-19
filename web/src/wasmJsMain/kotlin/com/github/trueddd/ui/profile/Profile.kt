package com.github.trueddd.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.trueddd.core.*
import com.github.trueddd.data.*
import com.github.trueddd.di.get
import com.github.trueddd.items.Usable
import com.github.trueddd.items.WheelItem
import com.github.trueddd.theme.Colors
import com.github.trueddd.ui.widget.AsyncImage
import com.github.trueddd.util.RelativeDate
import com.github.trueddd.util.localized
import com.github.trueddd.util.typeLocalized
import com.github.trueddd.utils.DefaultTimeZone
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.wheelItems
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

private object ProfileContentType {
    const val BACKGROUND = "background"
    const val STATS = "stats"
    const val HEADER = "header"
    const val DATE = "date"
    const val TABLE = "table"
}

private const val TAG = "ProfileScreen"

private val leftSideBarWidth = 320.dp
private val leftSideBarPadding = 32.dp

@Composable
fun ProfileScreen(
    currentParticipant: Participant?,
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot?,
    modifier: Modifier = Modifier,
) {
    val gameStateProvider = remember { get<GameStateProvider>() }
    val authManager = remember { get<AuthManager>() }
    var turnsHistory by remember { mutableStateOf<PlayersHistory?>(null) }
    LaunchedEffect(Unit) {
        gameStateProvider.playersHistoryFlow().collect { turnsHistory = it }
    }
    var selected by remember { mutableStateOf(currentParticipant ?: gameConfig.players.firstOrNull()) }
    PermanentNavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .fillMaxHeight()
                    .width(leftSideBarWidth)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
            ) {
                gameConfig.players.forEach {
                    NavigationDrawerItem(
                        label = { Text(it.displayName) },
                        selected = selected == it,
                        onClick = { selected = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )
                }
                if (currentParticipant == null) {
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Войти") },
                        selected = false,
                        onClick = { authManager.requestAuth() },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        },
        modifier = modifier
    ) {
        if (stateSnapshot != null && selected != null) {
            Profile(
                selectedPlayer = selected!!,
                currentPlayer = currentParticipant,
                gameConfig = gameConfig,
                stateSnapshot = stateSnapshot,
                turnsHistory = turnsHistory?.get(selected!!.name) ?: PlayerTurnsHistory(emptyList()),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WheelItemView(
    item: WheelItem,
    onUse: () -> Unit = {}
) {
    val router = remember { get<ServerRouter>() }
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(item.name)
                            }
                            append(" • ")
                            withStyle(SpanStyle(color = Color(item.color))) {
                                append(item.typeLocalized)
                            }
                        }
                    )
                },
                text = { Text(item.description) },
                action = if (item is Usable) { {
                    TextButton(
                        onClick = {
                            onUse()
                            tooltipState.dismiss()
                        }
                    ) {
                        Text("Использовать")
                    }
                } } else null,
                modifier = Modifier
            )
        },
        enableUserInput = false,
        state = tooltipState,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        scope.launch {
                            if (tooltipState.isVisible) tooltipState.dismiss() else tooltipState.show()
                        }
                    }
                )
                .background(Color(item.color), RoundedCornerShape(16.dp))
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            AsyncImage(
                model = router.wheelItemIconUrl(item.iconId),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun Profile(
    selectedPlayer: Participant,
    currentPlayer: Participant?,
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot,
    turnsHistory: PlayerTurnsHistory,
    modifier: Modifier = Modifier,
) {
    val commandSender = remember { get<CommandSender>() }
    var dialogItem by remember { mutableStateOf<WheelItem?>(null) }
    val lazyListState = rememberLazyListState()
    val leftSidePanelTopPadding by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.contentType == ProfileContentType.HEADER }
                ?.offset
        }
    }
    val turnsGroupedByDate = remember(turnsHistory) {
        turnsHistory.turns
            .sortedByDescending { it.moveDate }
            .groupBy { turn ->
                Instant.fromEpochMilliseconds(turn.moveDate).toLocalDateTime(DefaultTimeZone)
                    .let { RelativeDate.from(it) }
            }
    }
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .width(leftSideBarWidth)
                .align(Alignment.TopStart)
                .padding(top = 32.dp, bottom = 32.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(36.dp))
        )
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(vertical = 32.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item(contentType = ProfileContentType.BACKGROUND) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            item(contentType = ProfileContentType.STATS) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(start = leftSideBarWidth + leftSideBarPadding)
                        .padding(vertical = 36.dp)
                ) {
                    Stats(
                        expanded = true,
                        turnsHistory = turnsHistory,
                        modifier = Modifier
                            .weight(1f)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .weight(2f)
                    ) {
                        stateSnapshot.playersState[selectedPlayer.name]?.wheelItems?.forEach { item ->
                            WheelItemView(item) {
                                Log.info(TAG, "Called dialog for ${item.name}")
                                dialogItem = item
                            }
                        }
                    }
                }
            }
            stickyHeader(contentType = ProfileContentType.HEADER) {
                Box(
                    modifier = Modifier
                        .padding(start = leftSideBarWidth + leftSideBarPadding)
                        .fillMaxWidth()
                ) {
                    var undercoverHeight by remember { mutableStateOf(0) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(with(LocalDensity.current) { undercoverHeight.toDp() } + 32.dp)
                            .align(Alignment.TopCenter)
                            .background(MaterialTheme.colorScheme.surface)
                            .zIndex(0.5f)
                    )
                    ElevatedCard(
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                            .zIndex(0.6f)
                            .onSizeChanged { undercoverHeight = it.height / 2 }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(text = "Перемещение")
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(text = "Игра")
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(text = "Жанр")
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(text = "Статус")
                            }
                        }
                    }
                }
            }
            turnsGroupedByDate.forEach { (relativeDate, turns) ->
                item(contentType = ProfileContentType.DATE) {
                    Text(
                        text = relativeDate.localized,
                        color = Colors.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp, start = leftSideBarWidth + leftSideBarPadding)
                            .fillMaxWidth()
                    )
                }
                item(contentType = ProfileContentType.TABLE) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(start = leftSideBarWidth + leftSideBarPadding)
                            .fillMaxWidth()
                    ) {
                        turns.forEach { turn ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(text = turn.moveRange?.let { "${it.first} » ${it.last}" } ?: "-")
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(text = turn.game?.game?.name ?: "-")
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(text = turn.game?.game?.genre?.localized ?: "-")
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(text = turn.game?.status?.localized ?: "-")
                                }
                            }
                        }
                    }
                }
            }
        }
        val minPadding = with(LocalDensity.current) { 24.dp.roundToPx() }
        val shouldShowSideStats by remember {
            derivedStateOf {
                val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
                visibleItems.none { it.contentType == ProfileContentType.STATS }
                    .and(visibleItems.isNotEmpty())
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = leftSidePanelTopPadding
                    ?.let { if (it < minPadding) minPadding else it }
                    ?.let { min(120.dp, with(LocalDensity.current) { it.toDp() }) }
                    ?: 120.dp
                )
                .width(leftSideBarWidth)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(8.dp)
                    .background(Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = selectedPlayer.displayName,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            AnimatedVisibility(
                visible = shouldShowSideStats,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .padding(top = 64.dp)
            ) {
                Stats(
                    expanded = false,
                    turnsHistory = turnsHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
            }
        }
        var allItems by remember { mutableStateOf(emptyList<WheelItem>()) }
        LaunchedEffect(Unit) {
            allItems = wheelItems.sortedBy { it.name }
        }
        if (dialogItem != null && currentPlayer != null) {
            WheelItemUseDialog(
                item = dialogItem!!,
                gameConfig = gameConfig,
                stateSnapshot = stateSnapshot,
                player = currentPlayer,
                items = allItems,
                onItemUse = { item, parameters ->
                    Log.info(TAG, "using ${item.name} with parameters $parameters")
                    commandSender.sendCommand(Command.Action.itemUse(currentPlayer, item, parameters))
                    dialogItem = null
                },
                onDialogDismiss = { dialogItem = null }
            )
        }
    }
}
