package com.github.trueddd.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.Command
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GlobalState
import com.github.trueddd.di.get

@Composable
fun Dashboard(
    globalState: GlobalState,
    socketState: SocketState,
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionsBoard(
                globalState = globalState,
                socketState = socketState,
                sendAction = { appClient.sendCommand(Command.Action(it)) },
                modifier = Modifier
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
                .weight(1f)
        ) {
            val actions by appClient.getActionsFlow().collectAsState(emptyList())
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
