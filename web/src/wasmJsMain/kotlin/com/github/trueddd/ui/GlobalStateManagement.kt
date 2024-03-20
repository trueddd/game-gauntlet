package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.SocketState
import com.github.trueddd.theme.Colors

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
            .background(Colors.SecondaryBackground, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Button(
            onClick = onSaveRequested,
            enabled = socketState is SocketState.Connected,
            colors = ButtonDefaults.buttonColors(containerColor = Colors.Primary),
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = {
                Text(
                    text = "Save",
                    color = Colors.Text
                )
            }
        )
        Button(
            onClick = onRestoreRequested,
            enabled = socketState is SocketState.Connected,
            colors = ButtonDefaults.buttonColors(containerColor = Colors.Primary),
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = {
                Text(
                    text = "Load",
                    color = Colors.Text
                )
            }
        )
        Spacer(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(1.dp)
                .fillMaxWidth()
                .background(Colors.Text)
        )
        Button(
            onClick = onResetRequested,
            enabled = socketState is SocketState.Connected,
            colors = ButtonDefaults.buttonColors(containerColor = Colors.Primary),
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = {
                Text(
                    text = "Reset",
                    color = Colors.Text
                )
            }
        )
    }
}
