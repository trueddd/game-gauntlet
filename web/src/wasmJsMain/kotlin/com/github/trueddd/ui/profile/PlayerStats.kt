package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.trueddd.data.PlayerTurnsHistory

@Stable
private fun PlayerTurnsHistory.averageMoveDice(): String {
    return if (statistics.averageDice.isNaN()) {
        "-"
    } else {
        val string = statistics.averageDice.toString()
        string.substring(0 .. (string.indexOfFirst { !it.isDigit() } + 2))
    }
}

@Composable
fun Stats(
    expanded: Boolean,
    turnsHistory: PlayerTurnsHistory,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        StatsItem(
            title = "Средний показатель кубика",
            value = turnsHistory.averageMoveDice(),
            expanded = expanded
        )
        StatsItem(
            title = "Пройденных игр",
            value = turnsHistory.statistics.finishedGames.toString(),
            expanded = expanded
        )
        StatsItem(
            title = "Количество рероллов",
            value = turnsHistory.statistics.rerolledGames.toString(),
            expanded = expanded
        )
        StatsItem(
            title = "Количество дропов",
            value = turnsHistory.statistics.droppedGames.toString(),
            expanded = expanded
        )
    }
}

@Composable
private fun StatsItem(
    title: String,
    value: String,
    expanded: Boolean,
) {
    if (expanded) {
        Card(
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(text = value)
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            Text(text = value)
        }
    }
}
