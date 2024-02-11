package com.github.trueddd.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.AppClient
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
        ActionsBoard(
            globalState = globalState,
            socketState = socketState,
            sendAction = { appClient.sendAction(it) },
            modifier = Modifier
        )
        StateTable(
            globalState = globalState,
            modifier = Modifier
        )
    }
}
