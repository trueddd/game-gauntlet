package com.github.trueddd.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.Action
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.Command
import com.github.trueddd.core.CommandSender
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GameConfig
import com.github.trueddd.data.Participant
import com.github.trueddd.data.StateSnapshot
import com.github.trueddd.di.get

@Composable
fun Dashboard(
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot,
    socketState: SocketState,
    participant: Participant?,
    modifier: Modifier = Modifier,
) {
    val commandSender = remember { get<CommandSender>() }
    val appClient = remember { get<AppClient>() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .width(IntrinsicSize.Min)
        ) {
            ActionsBoard(
                gameConfig = gameConfig,
                socketState = socketState,
                sendAction = { commandSender.sendCommand(Command.Action(it)) },
                participant = participant,
                modifier = Modifier
                    .fillMaxWidth()
            )
            GlobalStateManagement(
                socketState = socketState,
                onSaveRequested = { commandSender.sendCommand(Command.Save) },
                onRestoreRequested = { commandSender.sendCommand(Command.Restore) },
                onResetRequested = { commandSender.sendCommand(Command.Reset) },
                modifier = Modifier
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            var actions by remember { mutableStateOf(emptyList<Action>()) }
            LaunchedEffect(Unit) {
                appClient.getActionsFlow()
                    .collect {
                        actions = it + actions
                    }
            }
            StateTable(
                stateSnapshot = stateSnapshot,
                modifier = Modifier
            )
            ActionsLog(
                actions = actions,
                modifier = Modifier
            )
        }
    }
}
