package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.*
import com.github.trueddd.theme.Colors
import io.ktor.util.date.*

@Composable
fun ActionsLog(
    actions: List<Action>,
    modifier: Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .background(Colors.SecondaryBackground, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        items(actions) {
            when (it) {
                is BoardMove -> BoardMove(it)
                is GameDrop -> GameDrop(it)
                is GameRoll -> GameRoll(it)
//                    is GameSet -> TODO()
//                    is GameStatusChange -> TODO()
                is ItemReceive -> ItemReceive(it)
//                    is ItemUse -> TODO()
                else -> Text(text = it.id.toString())
            }
        }
    }
}

private fun Long.formatDate(): String {
    return GMTDate(this).run {
        "$hours:$minutes:$seconds $dayOfMonth.${month.ordinal + 1}.$year"
    }
}

@Composable
private fun BoardMove(action: BoardMove) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Бросок на ход",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.rolledBy.displayName,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.diceValue.toString(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(2f)
        )
        Text(
            text = action.issuedAt.formatDate(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun ItemReceive(action: ItemReceive) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Получение предмета",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.receivedBy.displayName,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.item.name,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(2f)
        )
        Text(
            text = action.issuedAt.formatDate(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun GameRoll(action: GameRoll) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Ролл игры",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.participant.displayName,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.gameId.value.toString(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(2f)
        )
        Text(
            text = action.issuedAt.formatDate(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun GameDrop(action: GameDrop) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Ролл игры",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.rolledBy.displayName,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.diceValue.toString(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(2f)
        )
        Text(
            text = action.issuedAt.formatDate(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
    }
}
