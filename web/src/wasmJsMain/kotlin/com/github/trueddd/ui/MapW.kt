package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.trueddd.theme.Colors
import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MapW(globalState: GlobalState) {
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
            MapCell(0, null)
            globalState.gameGenreDistribution.genres.forEachIndexed { index, cell ->
                MapCell(index + 1, cell)
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

@Composable
private fun MapCell(index: Int, cell: Game.Genre?) {
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

