package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.*
import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

@Composable
fun ActionsLog(
    actions: List<Action>,
    modifier: Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        items(actions) {
            when (it) {
                is BoardMove -> BoardMove(it)
                is GameDrop -> GameDrop(it)
                is GameRoll -> GameRoll(it)
                is GameSet -> GameSet(it)
                is GameStatusChange -> GameStatusChange(it)
                is ItemReceive -> ItemReceive(it)
                is ItemUse -> ItemUse(it)
                is GlobalEvent -> GlobalEvent(it)
            }
        }
    }
}

private val Int.twoDigitString
    get() = this.toString().padStart(2, '0')

private fun Long.formatDate(): String {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(DefaultTimeZone)
        .run {
            buildString {
                append(hour.twoDigitString)
                append(":")
                append(minute.twoDigitString)
                append(":")
                append(second.twoDigitString)
                append(" ")
                append(dayOfMonth.twoDigitString)
                append(".")
                append(monthNumber.twoDigitString)
                append(".")
                append(year.twoDigitString)
            }
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

@Composable
private fun GameStatusChange(action: GameStatusChange) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Статус игры изменён",
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
            text = action.gameNewStatus.name,
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
private fun ItemUse(action: ItemUse) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Использование предмета",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.usedBy.displayName,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.itemUid,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.arguments.joinToString(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
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
private fun GlobalEvent(action: GlobalEvent) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Глобальное событие",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.type.name,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(2f)
        )
        Text(
            text = action.stageIndex.toString(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
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
private fun GameSet(action: GameSet) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Выбор игры",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = action.setBy.displayName,
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
