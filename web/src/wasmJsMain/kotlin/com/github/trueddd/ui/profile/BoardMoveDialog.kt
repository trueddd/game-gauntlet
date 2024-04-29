package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.trueddd.core.AppStorage
import com.github.trueddd.di.get
import com.github.trueddd.ui.widget.DiceAnimation
import com.github.trueddd.ui.widget.DiceD6
import com.github.trueddd.ui.widget.WarningTextBlock
import kotlin.math.roundToInt

@Composable
fun BoardMoveDialog(
    onMoveRequested: (Int) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    val appStorage = remember { get<AppStorage>() }
    var diceValue by remember { mutableStateOf(appStorage.getSavedDiceValue()) }
    AlertDialog(
        onDismissRequest = onDialogDismiss,
        properties = DialogProperties(),
        title = { Text("Новый ход") },
        dismissButton = {
            OutlinedButton(onClick = onDialogDismiss) {
                Text("Отмена")
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = { onMoveRequested(diceValue) },
            ) {
                Text("Сделать ход")
            }
        },
        modifier = Modifier,
        icon = null,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
            ) {
                Text(text = "Укажите значение кубика на ход (бросок кубика производится на вкладке Колеса):")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DiceD6(
                        diceValue,
                        diceAnimation = DiceAnimation(duration = 300),
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Slider(
                        value = diceValue.toFloat(),
                        onValueChange = { diceValue = it.roundToInt() },
                        steps = 4,
                        valueRange = 1f .. 6f,
                    )
                }
                WarningTextBlock(
                    text = "Это действие нельзя будет отменить.",
                )
            }
        },
    )
}
