package com.github.trueddd.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.Action
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.Command
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.di.get

@Composable
fun Dashboard(
    globalState: GlobalState,
    socketState: SocketState,
    participant: Participant?,
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            ActionsBoard(
                globalState = globalState,
                socketState = socketState,
                sendAction = { appClient.sendCommand(Command.Action(it)) },
                participant = participant,
                modifier = Modifier
                    .fillMaxWidth()
            )
            GlobalStateManagement(
                socketState = socketState,
                onSaveRequested = { appClient.sendCommand(Command.Save) },
                onRestoreRequested = { appClient.sendCommand(Command.Restore) },
                onResetRequested = { appClient.sendCommand(Command.Reset) },
                modifier = Modifier
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(3f)
        ) {
            var actions by remember { mutableStateOf(emptyList<Action>()) }
            LaunchedEffect(Unit) {
                appClient.getActionsFlow()
                    .collect {
                        actions = it + actions
                    }
            }
            StateTable(
                globalState = globalState,
                modifier = Modifier
            )
            ActionsLog(
                actions = actions,
                modifier = Modifier
            )
        }
    }
}
