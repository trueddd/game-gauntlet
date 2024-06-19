package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.trueddd.actions.Action
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GameConfig
import com.github.trueddd.data.Participant
import com.github.trueddd.util.isDevEnvironment

private val actions = mapOf(
    Action.Key.BoardMove to "Board Move",
    Action.Key.GameRoll to "Game Roll",
    Action.Key.GameSet to "Game Set",
    Action.Key.GameStatusChange to "Game Status Change",
    Action.Key.GameDrop to "Game Drop",
    Action.Key.ItemReceive to "Item Receive",
    Action.Key.ItemUse to "Item Use",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsBoard(
    gameConfig: GameConfig,
    socketState: SocketState,
    participant: Participant?,
    sendAction: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var user by remember(participant) { mutableStateOf(if (isDevEnvironment()) participant else null) }
    var action by remember { mutableStateOf(Action.Key.BoardMove) }
    var arguments by remember { mutableStateOf(TextFieldValue("")) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        var userExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = userExpanded,
            onExpandedChange = { userExpanded = !userExpanded },
            modifier = Modifier
        ) {
            OutlinedTextField(
                value = when {
                    user != null -> user!!.displayName
                    participant == null -> "You are unauthorized"
                    else -> "User not selected"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "User") },
                trailingIcon = {
                    if (isDevEnvironment()) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = userExpanded)
                    }
                },
                modifier = Modifier
                    .menuAnchor()
            )
            if (isDevEnvironment()) {
                ExposedDropdownMenu(
                    expanded = userExpanded,
                    onDismissRequest = { userExpanded = false },
                ) {
                    gameConfig.players.map { it.displayName }.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(it)
                            },
                            onClick = {
                                user = gameConfig.players.firstNotNullOfOrNull { key ->
                                    if (key.displayName == it) key else null
                                }
                                userExpanded = false
                            },
                        )
                    }
                }
            }
        }
        var actionExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = actionExpanded,
            onExpandedChange = { actionExpanded = !actionExpanded },
            modifier = Modifier
        ) {
            OutlinedTextField(
                value = actions[action] ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Action") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actionExpanded) },
                modifier = Modifier
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = actionExpanded,
                onDismissRequest = { actionExpanded = false }
            ) {
                actions.forEach { (key, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            action = key
                            actionExpanded = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = arguments,
            onValueChange = { arguments = it },
            singleLine = true,
            label = { Text("Arguments") },
            placeholder = { Text("Divided by `,`") },
            trailingIcon = {
                if (arguments.text.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear arguments",
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { arguments = arguments.copy(text = "") }
                    )
                }
            },
            modifier = Modifier
        )
        Button(
            onClick = {
                val readyUser = user?.name ?: return@Button
                val readyArguments = arguments.text.replace(" ", "").split(",")
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
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand),
            content = {
                Text(text = "Send")
            }
        )
    }
}
