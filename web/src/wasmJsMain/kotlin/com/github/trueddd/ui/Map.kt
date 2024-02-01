package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.theme.Colors
import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Map(globalState: GlobalState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FlowRow {
            GenreLegend(Game.Genre.Runner)
            GenreLegend(Game.Genre.ThreeInRow)
            GenreLegend(Game.Genre.Shooter)
            GenreLegend(Game.Genre.PointAndClick)
            GenreLegend(Game.Genre.Business)
            GenreLegend(Game.Genre.Puzzle)
            GenreLegend(Game.Genre.Special)
        }
        FlowRow {
            val playersPositions = remember(globalState.players) {
                globalState.players.entries.associate { (player, state) -> player to state.position }
            }
            MapCell(0, null, playersPositions)
            globalState.gameGenreDistribution.genres.forEachIndexed { index, cell ->
                MapCell(index + 1, cell, playersPositions)
            }
        }
    }
}

@Composable
private fun GenreLegend(genre: Game.Genre) {
    Text(
        text = genre.name,
        modifier = Modifier
            .background(genre.color)
            .padding(4.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MapCell(index: Int, cell: Game.Genre?, playersPositions: Map<Participant, Int>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(64.dp)
            .background(cell.color)
            .border(2.dp, Colors.Primary)
    ) {
        Text(
            text = "$index",
        )
        if (playersPositions.values.any { it == index }) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                playersPositions
                    .filterValues { it == index }
                    .forEach { (player, _) ->
                        Text(
                            text = player.displayName.first().uppercase(),
                            color = Colors.Primary,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White, CircleShape)
                                .border(2.dp, Colors.Primary, CircleShape)
                                .padding(4.dp)
                        )
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

