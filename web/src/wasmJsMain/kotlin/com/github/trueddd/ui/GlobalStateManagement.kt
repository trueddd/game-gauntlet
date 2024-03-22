package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.SocketState

@Composable
fun GlobalStateManagement(
    socketState: SocketState,
    onSaveRequested: () -> Unit,
    onRestoreRequested: () -> Unit,
    onResetRequested: () -> Unit,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Button(
            onClick = onSaveRequested,
            enabled = socketState is SocketState.Connected,
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = { Text(text = "Save") }
        )
        Button(
            onClick = onRestoreRequested,
            enabled = socketState is SocketState.Connected,
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = { Text(text = "Load") }
        )
        Button(
            onClick = onResetRequested,
            enabled = socketState is SocketState.Connected,
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = { Text(text = "Reset") }
        )
    }
}
