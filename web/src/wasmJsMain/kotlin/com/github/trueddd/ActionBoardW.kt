package com.github.trueddd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

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
fun RowScope.ActionsBoardW(
    globalState: GlobalState,
    sendAction: (String) -> Unit = {},
) {
    var user by remember { mutableStateOf<Participant?>(null) }
    var action by remember { mutableStateOf(Action.Key.BoardMove) }
    var arguments by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .weight(1f)
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
            text = "Arguments",
            color = Colors.Text,
            modifier = Modifier
                .padding(top = 8.dp)
        )
        TextField(
            value = arguments,
            onValueChange = { arguments = it },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(textColor = Colors.Text),
            modifier = Modifier
        )
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
                println("sending `$message`")
                sendAction(message)
            },
            enabled = user != null,
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
