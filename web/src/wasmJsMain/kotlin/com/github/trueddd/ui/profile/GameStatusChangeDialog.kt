package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.trueddd.data.Game
import com.github.trueddd.data.Participant
import com.github.trueddd.data.StateSnapshot
import com.github.trueddd.ui.widget.Dice
import com.github.trueddd.ui.widget.DiceType
import com.github.trueddd.ui.widget.WarningTextBlock
import com.github.trueddd.util.isDevEnvironment
import com.github.trueddd.util.localized
import kotlin.math.roundToInt

@Stable
private val allowedStatuses = listOf(Game.Status.Dropped, Game.Status.Rerolled, Game.Status.Finished)

@Stable
sealed class StatusChangeRequest {
    data object Finished : StatusChangeRequest()
    data object Rerolled : StatusChangeRequest()
    data class Dropped(val diceValue: Int) : StatusChangeRequest()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameStatusChangeDialog(
    player: Participant,
    stateSnapshot: StateSnapshot,
    onStatusChangeRequested: (StatusChangeRequest) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    var status by remember { mutableStateOf<Game.Status?>(null) }
    var diceValue by remember { mutableStateOf(1) }
    AlertDialog(
        onDismissRequest = onDialogDismiss,
        properties = DialogProperties(),
        title = { Text("Статус игры") },
        dismissButton = {
            OutlinedButton(onClick = onDialogDismiss) {
                Text("Отмена")
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = click@ {
                    val request = when (status) {
                        Game.Status.Finished -> StatusChangeRequest.Finished
                        Game.Status.Rerolled -> StatusChangeRequest.Rerolled
                        Game.Status.Dropped -> StatusChangeRequest.Dropped(diceValue)
                        else -> return@click
                    }
                    onStatusChangeRequested(request)
                },
                enabled = status in allowedStatuses
            ) {
                Text("Использовать")
            }
        },
        modifier = Modifier,
        icon = null,
        text = {
            val gameName = stateSnapshot.playersState[player.name]?.currentGame?.game?.name
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Min)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Изменить статус игры ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(gameName)
                        }
                        append(" на:")
                    }
                )
                var statusExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier
                ) {
                    OutlinedTextField(
                        value = status?.localized ?: "-",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Статус") },
                        trailingIcon = {
                            if (isDevEnvironment()) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                    ) {
                        allowedStatuses.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.localized) },
                                onClick = {
                                    status = item
                                    statusExpanded = false
                                },
                            )
                        }
                    }
                }
                if (status == Game.Status.Dropped) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Dice(diceValue, DiceType.D6)
                        Slider(
                            value = diceValue.toFloat(),
                            onValueChange = { diceValue = it.roundToInt() },
                            steps = 4,
                            valueRange = 1f .. 6f,
                        )
                    }
                }
                WarningTextBlock(
                    text = "Это действие нельзя будет отменить.",
                )
            }
        },
    )
}
