package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.github.trueddd.data.*
import com.github.trueddd.items.BoardTrap
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.localized

@Stable
data class MapCellState(
    val index: Int,
    val genre: Game.Genre?,
    val players: List<Participant>,
    val traps: List<BoardTrap>,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Map(
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot?
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
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
                        traps = stateSnapshot?.boardTraps?.filterKeys { it == 0 }?.values?.toList() ?: emptyList()
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
                            traps = stateSnapshot?.boardTraps?.filterKeys { it == index + 1 }?.values?.toList() ?: emptyList()
                        )
                    )
                }
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
                .background(
                    color = state.genre?.color?.let { Color(it) } ?: Colors.DefaultGenre,
                    shape = RoundedCornerShape(8.dp)
                )
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
