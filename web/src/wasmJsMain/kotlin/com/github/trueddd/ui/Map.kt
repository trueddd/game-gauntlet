package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.items.BoardTrap
import com.github.trueddd.theme.Colors

@Stable
data class MapCellState(
    val index: Int,
    val genre: Game.Genre?,
    val players: List<Participant>,
    val traps: List<BoardTrap>,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Map(globalState: GlobalState) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        val mapState = remember(globalState) {
            buildList {
                add(MapCellState(
                    index = 0,
                    genre = null,
                    players = globalState.players.filterValues { it.position == 0 }.keys.toList(),
                    traps = globalState.boardTraps.filterKeys { it == 0 }.values.toList()
                ))
                globalState.gameGenreDistribution.genres.forEachIndexed { index, genre ->
                    add(MapCellState(
                        index = index + 1,
                        genre = genre,
                        players = globalState.players.filterValues { it.position == index + 1 }.keys.toList(),
                        traps = globalState.boardTraps.filterKeys { it == index + 1 }.values.toList()
                    ))
                }
            }
        }
        FlowRow {
            mapState.forEach {
                MapCell(it)
            }
        }
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
                .background(state.genre.color)
                .border(2.dp, MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                text = state.index.toString(),
                color = MaterialTheme.colorScheme.onBackground
            )
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

@Stable
private val Game.Genre?.color: Color
    get() = when (this) {
        null -> Colors.Genre.Default
        Game.Genre.Runner -> Colors.Genre.Runner
        Game.Genre.Business -> Colors.Genre.Business
        Game.Genre.Puzzle -> Colors.Genre.Puzzle
        Game.Genre.PointAndClick -> Colors.Genre.PointAndClick
        Game.Genre.Shooter -> Colors.Genre.Shooter
        Game.Genre.ThreeInRow -> Colors.Genre.ThreeInRow
        Game.Genre.Special -> Colors.Genre.Special
    }

@Stable
private val Game.Genre.localized: String
    get() = when (this) {
        Game.Genre.Runner -> "Бегалки"
        Game.Genre.Business -> "Бизнес"
        Game.Genre.Puzzle -> "Головоломки"
        Game.Genre.PointAndClick -> "Поиск предметов"
        Game.Genre.Shooter -> "Стрелялки"
        Game.Genre.ThreeInRow -> "Три в ряд"
        Game.Genre.Special -> "Специальный сектор"
    }
