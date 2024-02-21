package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .background(Colors.SecondaryBackground, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Button(
            onClick = onSaveRequested,
            enabled = socketState is SocketState.Connected,
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.Primary),
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
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.Primary),
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = {
                Text(
                    text = "Load",
                    color = Colors.Text
                )
            }
        )
    }
}
