package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.github.trueddd.ui.widget.WarningTextBlock
import com.github.trueddd.util.isDevEnvironment
import com.github.trueddd.util.localized

@Stable
private val allowedStatuses = listOf(Game.Status.Dropped, Game.Status.Rerolled, Game.Status.Finished)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameStatusChangeDialog(
    player: Participant,
    stateSnapshot: StateSnapshot,
    onStatusChangeRequested: (Game.Status) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    var status by remember { mutableStateOf<Game.Status?>(null) }
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
                onClick = { onStatusChangeRequested(status!!) },
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                WarningTextBlock(
                    text = "Это действие нельзя будет отменить.",
                )
            }
        },
    )
}
