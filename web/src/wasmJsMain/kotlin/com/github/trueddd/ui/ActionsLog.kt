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
import com.github.trueddd.data.GameConfig
import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

@Composable
fun ActionsLog(
    actions: List<Action>,
    gameConfig: GameConfig,
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
                is BoardMove -> BoardMoveAction(it, gameConfig)
                is GameDrop -> GameDropAction(it, gameConfig)
                is GameRoll -> GameRollAction(it, gameConfig)
                is GameSet -> GameSetAction(it, gameConfig)
                is GameStatusChange -> GameStatusChangeAction(it, gameConfig)
                is ItemReceive -> ItemReceiveAction(it, gameConfig)
                is ItemUse -> ItemUseAction(it, gameConfig)
                is GlobalEvent -> GlobalEventAction(it)
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
private fun BoardMoveAction(action: BoardMove, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.rolledBy),
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
private fun ItemReceiveAction(action: ItemReceive, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.receivedBy),
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
private fun GameRollAction(action: GameRoll, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.playerName),
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
private fun GameDropAction(action: GameDrop, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.rolledBy),
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
private fun GameStatusChangeAction(action: GameStatusChange, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.playerName),
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
private fun ItemUseAction(action: ItemUse, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.usedBy),
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
private fun GlobalEventAction(action: GlobalEvent) {
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
private fun GameSetAction(action: GameSet, gameConfig: GameConfig) {
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
            text = gameConfig.displayNameOf(action.setBy),
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
