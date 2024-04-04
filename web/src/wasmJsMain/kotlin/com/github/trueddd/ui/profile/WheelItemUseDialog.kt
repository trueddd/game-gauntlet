package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.items.*
import com.github.trueddd.util.isDevEnvironment

@Composable
fun WheelItemUseDialog(
    item: WheelItem,
    globalState: GlobalState,
    player: Participant,
    items: List<WheelItem>,
    onItemUse: (WheelItem, List<String>) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDialogDismiss,
        properties = DialogProperties(),
        title = { Text("Использовать ${item.name}") },
        dismissButton = {
            TextButton(onClick = onDialogDismiss) {
                Text("Отмена")
            }
        },
        confirmButton = {
            TextButton(onClick = {}) {
                Text("Использовать")
            }
        },
        modifier = Modifier,
        icon = null,
        text = {
            Column {
                Text(item.description)
                if (item is Parametrized<*>) {
                    Spacer(modifier = Modifier.height(32.dp))
//                    var parameters by remember { mutableMapOf(item.parametersScheme.associate { it to null }) }
                    item.parametersScheme.forEach { parameter ->
                        when (parameter) {
                            is ParameterType.Bool -> BoolParameter(parameter)
                            is ParameterType.Int -> IntParameter(parameter)
                            is ParameterType.Player -> PlayerParameter(parameter, globalState)
                            is ParameterType.Item -> ItemParameter(parameter, globalState, items, player)
                            is ParameterType.Genre -> GenreParameter(parameter)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun BoolParameter(parameter: ParameterType.Bool) {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = parameter.name,
            modifier = Modifier
                .weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
        )
    }
}

@Composable
private fun IntParameter(parameter: ParameterType.Int) {
    var value by remember { mutableStateOf(0) }
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { newValue -> newValue.toIntOrNull()?.let { value = it } },
        label = { Text(text = parameter.name) },
        modifier = Modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerParameter(parameter: ParameterType.Player, globalState: GlobalState) {
    var value by remember { mutableStateOf<Participant?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val values = remember { globalState.players.keys.filter(parameter.predicate).map { it.displayName } }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = value?.displayName ?: "-",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = parameter.name) },
            trailingIcon = {
                if (isDevEnvironment()) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            values.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        value = globalState.players.firstNotNullOfOrNull { (key, _) ->
                            if (key.displayName == it) key else null
                        }
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemParameter(
    parameter: ParameterType.Item,
    globalState: GlobalState,
    items: List<WheelItem>,
    player: Participant
) {
    var value by remember { mutableStateOf<WheelItem?>(null) }
    var itemExpanded by remember { mutableStateOf(false) }
    var playerExpanded by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf<Participant?>(null) }
    val values = remember(selectedPlayer) {
        when (parameter.itemSetType) {
            ItemSetType.Personal -> globalState.stateOf(player).wheelItems.filter(parameter.predicate)
            ItemSetType.Foreign -> if (selectedPlayer != null) {
                globalState.stateOf(selectedPlayer!!).wheelItems.filter(parameter.predicate)
            } else {
                emptyList()
            }
            ItemSetType.Common -> items
        }
    }
    Column {
        if (parameter.itemSetType == ItemSetType.Foreign) {
            val players = remember {
                globalState.players.keys.filter{ it != player }
            }
            ExposedDropdownMenuBox(
                expanded = playerExpanded,
                onExpandedChange = { playerExpanded = !playerExpanded },
                modifier = Modifier
            ) {
                OutlinedTextField(
                    value = selectedPlayer?.displayName ?: "-",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Цель") },
                    trailingIcon = {
                        if (isDevEnvironment()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerExpanded)
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = playerExpanded,
                    onDismissRequest = { playerExpanded = false },
                ) {
                    players.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.displayName) },
                            onClick = {
                                selectedPlayer = item
                                playerExpanded = false
                            },
                        )
                    }
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = itemExpanded,
            onExpandedChange = { itemExpanded = !itemExpanded },
            modifier = Modifier
        ) {
            OutlinedTextField(
                value = value?.name ?: "-",
                onValueChange = {},
                readOnly = true,
                label = { Text(text = parameter.name) },
                trailingIcon = {
                    if (isDevEnvironment()) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded)
                    }
                },
                modifier = Modifier
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = itemExpanded,
                onDismissRequest = { itemExpanded = false },
            ) {
                values.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.name) },
                        onClick = {
                            value = item
                            itemExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreParameter(parameter: ParameterType.Genre) {
    var value by remember { mutableStateOf<Game.Genre?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val values = remember { Game.Genre.entries }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = value?.name ?: "-",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = parameter.name) },
            trailingIcon = {
                if (isDevEnvironment()) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            values.forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        value = it
                        expanded = false
                    },
                )
            }
        }
    }
}
