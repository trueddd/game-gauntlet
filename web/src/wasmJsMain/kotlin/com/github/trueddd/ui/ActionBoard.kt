package com.github.trueddd.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.Action
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.theme.Colors

private val actions = mapOf(
    Action.Key.BoardMove to "Board Move",
    Action.Key.GameRoll to "Game Roll",
    Action.Key.GameSet to "Game Set",
    Action.Key.GameStatusChange to "Game Status Change",
    Action.Key.GameDrop to "Game Drop",
    Action.Key.ItemReceive to "Item Receive",
    Action.Key.ItemUse to "Item Use",
)

@Composable
fun ActionsBoard(
    globalState: GlobalState,
    socketState: SocketState,
    sendAction: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var user by remember { mutableStateOf<Participant?>(null) }
    var action by remember { mutableStateOf(Action.Key.BoardMove) }
    var arguments by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .background(Colors.SecondaryBackground, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "User",
            color = Colors.Text
        )
        Dropdown(
            globalState.players.keys.map { it.displayName },
            onOptionSelected = { name ->
                user = globalState.players.firstNotNullOfOrNull { (key, _) ->
                    if (key.displayName == name) key else null
                }
            },
            modifier = Modifier
        )
        Text(
            text = "Action",
            color = Colors.Text,
            modifier = Modifier
                .padding(top = 8.dp)
        )
        Dropdown(
            actions.values.toList(),
            onOptionSelected = { name ->
                action = actions.firstNotNullOfOrNull { (key, value) ->
                    if (value == name) key else null
                } ?: Action.Key.BoardMove
            },
            modifier = Modifier
        )
        Text(
            text = "Arguments (divided by `,`)",
            color = Colors.Text,
            modifier = Modifier
                .padding(top = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                value = arguments,
                onValueChange = { arguments = it },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(textColor = Colors.Text),
                modifier = Modifier
                    .weight(1f)
            )
            AnimatedVisibility(
                visible = arguments.isNotEmpty(),
                enter = expandHorizontally(),
                exit = shrinkHorizontally(),
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear arguments",
                    tint = Colors.Primary,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable { arguments = "" }
                )
            }
        }
        Button(
            onClick = {
                val readyUser = user?.name ?: return@Button
                val readyArguments = arguments.replace(" ", "").split(",")
                val message = buildString {
                    append(readyUser)
                    append(":")
                    append(action)
                    readyArguments.forEach {
                        append(":$it")
                    }
                }.removeSuffix(":")
                sendAction(message)
            },
            enabled = user != null && socketState is SocketState.Connected,
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.Primary),
            modifier = Modifier
                .padding(top = 8.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            content = {
                Text(
                    text = "Send",
                    color = Colors.Text
                )
            }
        )
    }
}
