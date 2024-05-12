package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.AuthManager
import com.github.trueddd.di.get
import kotlinx.coroutines.launch

@Composable
fun TwitchRewardDialog(
    onDialogDismiss: () -> Unit,
) {
    val appClient = remember { get<AppClient>() }
    val authManager = remember { get<AuthManager>() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var rewardCreated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLoading = true
        val token = authManager.savedJwtToken() ?: return@LaunchedEffect
        rewardCreated = appClient.checkTwitchRewardAvailability(token).isSuccess
        isLoading = false
    }
    AlertDialog(
        onDismissRequest = onDialogDismiss,
        properties = DialogProperties(),
        title = { Text("Награды Twitch") },
        dismissButton = {
            OutlinedButton(
                onClick = onDialogDismiss,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Закрыть")
            }
        },
        confirmButton = {},
        modifier = Modifier,
        icon = null,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
            ) {
                Text(
                    text = "Данная награда будет использоваться для сбора баллов на глобальные события (Ядерная бомба, Торнадо)."
                )
                when {
                    isLoading -> {
                        LinearProgressIndicator()
                        Text("Проверяем доступность наград...")
                    }
                    rewardCreated -> {
                        Text("Награда готова к использованию, изменить её параметры можно в Панели управления Twitch.")
                    }
                    else -> {
                        Text("Награда не создана")
                        OutlinedButton(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    val token = authManager.savedJwtToken() ?: return@launch
                                    rewardCreated = appClient.createTwitchReward(token).isSuccess
                                    isLoading = false
                                }
                            },
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Text("Создать награду")
                        }
                    }
                }
            }
        },
    )
}
