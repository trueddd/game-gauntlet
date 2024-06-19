package com.github.trueddd.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameConfig
import com.github.trueddd.data.Participant
import com.github.trueddd.data.StateSnapshot
import com.github.trueddd.items.ParameterType
import com.github.trueddd.items.Parametrized
import com.github.trueddd.items.WheelItem

private fun Map<String, String>.updateParametersMap(
    currentParameter: ParameterType,
    newValue: String
): Map<String, String> {
    return mapValues { (key, value) -> if (key == currentParameter.name) newValue else value }
}

@Composable
fun WheelItemUseDialog(
    item: WheelItem,
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot,
    player: Participant,
    items: List<WheelItem>,
    onItemUse: (WheelItem, List<String>) -> Unit,
    onDialogDismiss: () -> Unit,
) {
    var parameters by remember {
        if (item is Parametrized<*>) {
            mutableStateOf(item.parametersScheme.associate { it.name to "" })
        } else {
            mutableStateOf(emptyMap())
        }
    }
    val canUse = remember {
        derivedStateOf { parameters.all { it.value.isNotBlank() } }
    }
    LaunchedEffect(parameters) {
        println("changed parameters: $parameters")
    }
    AlertDialog(
        onDismissRequest = onDialogDismiss,
        properties = DialogProperties(),
        title = { Text("Использовать ${item.name}") },
        dismissButton = {
            TextButton(
                onClick = onDialogDismiss,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Отмена")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val arguments = if (item is Parametrized<*>) {
                        item.parametersScheme.map { parameters[it.name]!! }
                    } else {
                        emptyList()
                    }
                    onItemUse(item, arguments)
                },
                enabled = canUse.value,
                modifier = Modifier
                    .pointerHoverIcon(if (canUse.value) PointerIcon.Hand else PointerIcon.Default)
            ) {
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
                    item.parametersScheme.forEach { parameter ->
                        when (parameter) {
                            is ParameterType.Bool -> BoolParameter(parameter) {
                                parameters = parameters.updateParametersMap(parameter, if (it) "1" else "0")
                            }
                            is ParameterType.Int -> IntParameter(parameter) {
                                parameters = parameters.updateParametersMap(parameter, it.toString())
                            }
                            is ParameterType.Player -> PlayerParameter(parameter, gameConfig) {
                                parameters = parameters.updateParametersMap(parameter, it.name)
                            }
                            is ParameterType.ForeignItem -> ForeignItemParameter(parameter, stateSnapshot, gameConfig, player) {
                                parameters = parameters.updateParametersMap(parameter, it.uid)
                            }
                            is ParameterType.MyItem -> MyItemParameter(parameter, stateSnapshot, player) {
                                parameters = parameters.updateParametersMap(parameter, it.uid)
                            }
                            is ParameterType.Item -> ItemParameter(parameter, items) {
                                parameters = parameters.updateParametersMap(parameter, it.id.asString())
                            }
                            is ParameterType.Genre -> GenreParameter(parameter) {
                                parameters = parameters.updateParametersMap(parameter, it.ordinal.toString())
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun BoolParameter(
    parameter: ParameterType.Bool,
    onParameterUpdated: (Boolean) -> Unit = {}
) {
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
            onCheckedChange = {
                checked = it
                onParameterUpdated(it)
            },
        )
    }
}

@Composable
private fun IntParameter(
    parameter: ParameterType.Int,
    onParameterUpdated: (Int) -> Unit
) {
    var value by remember { mutableStateOf(0) }
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { newValue ->
            newValue.toIntOrNull()?.let {
                value = it
                onParameterUpdated(it)
            }
        },
        label = { Text(text = parameter.name) },
        modifier = Modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerParameter(
    parameter: ParameterType.Player,
    gameConfig: GameConfig,
    onParameterUpdated: (Participant) -> Unit
) {
    var value by remember { mutableStateOf<Participant?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val values = remember { gameConfig.players.filter(parameter.predicate).map { it.displayName } }
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
            supportingText = { if (parameter.description != null) Text(text = parameter.description!!) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                        value = gameConfig.players.firstNotNullOfOrNull { key->
                            if (key.displayName == it) key else null
                        }
                        value?.let(onParameterUpdated)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForeignItemParameter(
    parameter: ParameterType.ForeignItem,
    stateSnapshot: StateSnapshot,
    gameConfig: GameConfig,
    player: Participant,
    onParameterUpdated: (WheelItem) -> Unit
) {
    var value by remember { mutableStateOf<WheelItem?>(null) }
    var itemExpanded by remember { mutableStateOf(false) }
    var playerExpanded by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf<Participant?>(null) }
    val values = remember(selectedPlayer, stateSnapshot) {
        if (selectedPlayer != null) {
            stateSnapshot.playersState[selectedPlayer!!.name]?.wheelItems?.filter(parameter.predicate) ?: emptyList()
        } else {
            emptyList()
        }
    }
    Column {
        val players = remember {
            gameConfig.players.filter{ it != player }
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerExpanded) },
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
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
                            onParameterUpdated(item)
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
private fun MyItemParameter(
    parameter: ParameterType.MyItem,
    stateSnapshot: StateSnapshot,
    player: Participant,
    onParameterUpdated: (WheelItem) -> Unit
) {
    var value by remember { mutableStateOf<WheelItem?>(null) }
    var itemExpanded by remember { mutableStateOf(false) }
    val values = remember(stateSnapshot.playersState[player.name]) {
        stateSnapshot.playersState[player.name]!!.wheelItems.filter(parameter.predicate)
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
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
                        onParameterUpdated(item)
                        itemExpanded = false
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
    items: List<WheelItem>,
    onParameterUpdated: (WheelItem) -> Unit
) {
    var value by remember { mutableStateOf<WheelItem?>(null) }
    var itemExpanded by remember { mutableStateOf(false) }
    val values = remember {
        items.filter(parameter.predicate)
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
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
                        onParameterUpdated(item)
                        itemExpanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreParameter(
    parameter: ParameterType.Genre,
    onParameterUpdated: (Game.Genre) -> Unit
) {
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                        onParameterUpdated(it)
                        expanded = false
                    },
                )
            }
        }
    }
}
