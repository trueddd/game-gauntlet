package com.github.trueddd

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import dev.fritz2.core.*
import dev.fritz2.remote.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val decoder = Json {
        allowStructuredMapKeys = true
    }
    val socket = websocket("ws://localhost:8102/state").connect()
    val globalState = socket.messages.body
        .onEach { console.log(it) }
        .filterNot { it.startsWith("YOU") }
        .map { decoder.decodeFromString<GlobalState>(it) }
        .catch { console.error(it.message) }
        .stateIn(GlobalScope, SharingStarted.Eagerly, null)
    render("#target") {
        val user = storeOf<Participant?>(null)
        val action = storeOf<Int?>(null)
        val arguments = storeOf("")
        h1 { + "AGG2 page" }
        table {
            tr {
                td {
                    userSelector(user, globalState)
                }
                td {
                    actionSelector(action)
                }
                td {
                    argumentsInput(arguments)
                }
                td {
                    button {
                        + "Send"
                        disabled(socket.isOpen.map { !it })
                        clicks handledBy { sendAction(socket, user, action, arguments) }
                    }
                }
            }
        }
        br {}
        renderState(globalState)
    }
}

private fun RenderContext.renderState(globalStateFlow: StateFlow<GlobalState?>) {
    globalStateFlow.filterNotNull().render { state ->
        div {
            p {
                + state.toString()
            }
        }
        table {
            tr {
                th {
                    a { +"Player" }
                }
                state.players.keys.forEach {
                    th {
                        a { + it.name }
                    }
                }
            }
            tr {
                td {
                    a { + "Position" }
                }
                state.players.values.forEach {
                    td {
                        a { + it.position.toString() }
                    }
                }
            }
            tr {
                td {
                    a { + "Game" }
                }
                state.players.values.forEach { playerState ->
                    td {
                        playerState.currentGame?.game?.name?.let { a { + it } }
                    }
                }
            }
            tr {
                td {
                    a { + "Inventory" }
                }
                state.players.values.forEach { playerState ->
                    td {
                        a { + playerState.inventory.joinToString { "(${it.name})[${it.uid}]" } }
                    }
                }
            }
            tr {
                td {
                    a { + "Effects" }
                }
                state.players.values.forEach { playerState ->
                    td {
                        a { + playerState.effects.joinToString { "(${it.name})[${it.uid}]" } }
                    }
                }
            }
            tr {
                td {
                    a { + "Pending events" }
                }
                state.players.values.forEach { playerState ->
                    td {
                        a { + playerState.pendingEvents.joinToString { "(${it.name})[${it.uid}]" } }
                    }
                }
            }
        }
    }
}

private suspend fun sendAction(
    socketSession: Session,
    userStore: Store<Participant?>,
    actionStore: Store<Int?>,
    argumentsStore: Store<String>,
) {
    val user = userStore.current ?: return
    val action = actionStore.current ?: return
    val arguments = argumentsStore.current.replace(" ", "").split(",")
    val message = buildString {
        append(user.name)
        append(":")
        append(action)
        arguments.forEach {
            append(":$it")
        }
    }.removeSuffix(":")
    console.log("sending `$message`")
    socketSession.send(message)
    userStore.update(null)
    actionStore.update(null)
    argumentsStore.update("")
}

private fun RenderContext.argumentsInput(argumentsStore: Store<String>) {
    div {
        label {
            + "Arguments (separated by `,`)"
            `for`(argumentsStore.id)
        }
        div {
            input(id = argumentsStore.id) {
                value(argumentsStore.data)
                changes.values() handledBy argumentsStore.update
            }
        }
    }
}

private fun RenderContext.userSelector(userStore: Store<Participant?>, globalStateFlow: StateFlow<GlobalState?>) {
    label {
        + "User"
        `for`(userStore.id)
    }
    globalStateFlow.renderNotNull { state ->
        state.players.keys.forEach { player ->
            div {
                input {
                    id(player.name)
                    name("username")
                    type("radio")
                    changes.values()
                        .map { player }
                        .handledBy(userStore.update)
                }
                label {
                    `for`(player.name)
                    + player.displayName
                }
            }
        }
    }
}

private fun RenderContext.actionSelector(actionStore: Store<Int?>) {
    val actions = mapOf(
        Action.Key.BoardMove to "Board Move",
        Action.Key.GameRoll to "Game Roll",
        Action.Key.GameSet to "Game Set",
        Action.Key.GameStatusChange to "Game Status Change",
        Action.Key.GameDrop to "Game Drop",
        Action.Key.ItemReceive to "Item Receive",
        Action.Key.ItemUse to "Item Use",
    )
    label {
        + "Action"
        `for`(actionStore.id)
    }
    actions.forEach { (actionId, name) ->
        div {
            input {
                id("action-$actionId")
                name("action")
                type("radio")
                changes.values().map { actionId } handledBy actionStore.update
            }
            label {
                `for`("action-$actionId")
                + name
            }
        }
    }
}
