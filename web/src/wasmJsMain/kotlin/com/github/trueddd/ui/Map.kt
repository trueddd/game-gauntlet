package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.actions.GlobalEvent
import com.github.trueddd.data.*
import com.github.trueddd.items.BoardTrap
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.localized
import com.github.trueddd.utils.GlobalEventConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlin.math.roundToInt

@Immutable
data class MapCellState(
    val index: Int,
    val genre: Game.Genre?,
    val players: List<Participant>,
    val traps: List<BoardTrap>,
    val radioStation: RadioStation,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Map(
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot?,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(32.dp)
            .fillMaxWidth()
    ) {
        val mapState = remember(stateSnapshot, gameConfig) {
            buildList {
                add(
                    MapCellState(
                        index = 0,
                        genre = null,
                        players = stateSnapshot?.playersState
                            ?.filterValues { it.position == 0 }?.keys
                            ?.let { names -> gameConfig.players.filter { it.name in names } }
                            ?: emptyList(),
                        traps = stateSnapshot?.boardTraps?.filterKeys { it == 0 }?.values?.toList() ?: emptyList(),
                        radioStation = gameConfig.radioCoverage.stationAt(position = 0),
                    )
                )
                gameConfig.gameGenreDistribution.genres.forEachIndexed { index, genre ->
                    add(
                        MapCellState(
                            index = index + 1,
                            genre = genre,
                            players = stateSnapshot?.playersState
                                ?.filterValues { it.position == index + 1 }?.keys
                                ?.let { names -> gameConfig.players.filter { it.name in names } }
                                ?: emptyList(),
                            traps = stateSnapshot?.boardTraps?.filterKeys { it == index + 1 }?.values?.toList() ?: emptyList(),
                            radioStation = gameConfig.radioCoverage.stationAt(position = index + 1),
                        )
                    )
                }
            }
        }
        if (stateSnapshot != null) {
            if (stateSnapshot.scheduledEvent != null) {
                GlobalEventAnnouncement(
                    scheduledEvent = stateSnapshot.scheduledEvent!!,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                GlobalEventCounter(
                    pointsAmount = stateSnapshot.overallAmountOfPointsRaised % GlobalEventConstants.EVENT_CAP,
                    pointsCap = GlobalEventConstants.EVENT_CAP,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            mapState.forEach {
                MapCell(it)
            }
        }
    }
}

@Composable
private fun GlobalEventAnnouncement(
    scheduledEvent: ScheduledEvent,
    modifier: Modifier = Modifier,
) {
    var timer by remember { mutableStateOf("") }
    LaunchedEffect(scheduledEvent) {
        while (isActive) {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val remaining = scheduledEvent.startTime - currentTime
            val hours = (remaining / 1000 / 3600 % 60).toString()
            val minutes = (remaining / 1000 / 60 % 60).toString().padStart(2, '0')
            val seconds = (remaining / 1000 % 60).toString().padStart(2, '0')
            timer = "$hours:$minutes:$seconds"
            delay(1000 - (Clock.System.now().toEpochMilliseconds() - currentTime))
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .background(Colors.Error, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = buildString {
                when (scheduledEvent.eventType) {
                    GlobalEvent.Type.Tornado -> "Внимание! Приближается ураган!"
                    GlobalEvent.Type.Nuke -> "Внимание! Ожидается ядерный удар!"
                }.let { append(it) }
                if (timer.isNotEmpty()) {
                    append(" До начала: ")
                    append(timer)
                }
            },
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun GlobalEventCounter(
    pointsAmount: Long,
    pointsCap: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .border(2.dp, Colors.Error, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = buildString {
                append("Собрано баллов на глобальное событие: ")
                append(pointsAmount)
                append(" из ")
                append(pointsCap)
                append(" (")
                append(pointsAmount.toDouble().div(pointsCap).times(100).roundToInt())
                append("%)")
            },
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MapCell(state: MapCellState) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = {
                    Text(
                        text = if (state.index == 0) "Стартовая клетка" else "Клетка ${state.index}",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        when (state.genre) {
                            Game.Genre.Special -> Text("Специальный сектор")
                            null -> {}
                            else -> Text("Жанр: ${state.genre.localized}")
                        }
                        if (state.players.isNotEmpty()) {
                            Text("Игроки на клетке: ${state.players.joinToString { it.displayName }}")
                        }
                        if (state.traps.isNotEmpty()) {
                            Text("Ловушки: ${state.traps.joinToString { it.name }}")
                        }
                        Text("Радиостанция: ${state.radioStation.localized}")
                    }
                }
            )
        },
        state = rememberTooltipState(isPersistent = true),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = state.genre?.color?.let { Color(it) } ?: Colors.DefaultGenre,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            if (state.index != 0) {
                Text(
                    text = state.index.toString(),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (state.players.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    state.players.forEach {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.onSecondaryContainer, CircleShape)
                        ) {
                            Text(
                                text = it.displayName.first().uppercase(),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
