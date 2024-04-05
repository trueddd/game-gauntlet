package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.Action
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

private fun Participant.averageMoveDice(actions: List<Action>): String {
    val average = actions.filterIsInstance<BoardMove>()
        .filter { it.rolledBy == this }
        .map { it.diceValue }
        .average()
    return if (average.isNaN()) {
        "-"
    } else {
        val string = average.toString()
        string.substring(0 .. (string.indexOfFirst { !it.isDigit() } + 2))
    }
}

private fun Participant.gamesWithStatus(status: Game.Status, globalState: GlobalState): String {
    return globalState.stateOf(this)
        .gameHistory
        .count { it.status == status }
        .toString()
}

@Composable
fun Stats(
    expanded: Boolean,
    player: Participant,
    globalState: GlobalState,
    actions: List<Action>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        StatsItem(
            title = "Средний показатель кубика",
            value = player.averageMoveDice(actions),
            expanded = expanded
        )
        StatsItem(
            title = "Пройденных игр",
            value = player.gamesWithStatus(Game.Status.Finished, globalState),
            expanded = expanded
        )
        StatsItem(
            title = "Количество рероллов",
            value = player.gamesWithStatus(Game.Status.Rerolled, globalState),
            expanded = expanded
        )
        StatsItem(
            title = "Количество дропов",
            value = player.gamesWithStatus(Game.Status.Dropped, globalState),
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
