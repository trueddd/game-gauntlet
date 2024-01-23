package com.github.trueddd

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import dev.fritz2.core.*
import dev.fritz2.remote.Session
import dev.fritz2.remote.isOpen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

private fun RenderContext.argumentsInput(argumentsStore: Store<String>) {
    div {
        label("block mb-2 text-sm font-medium text-gray-900 dark:text-white") {
            + "Arguments"
            `for`(argumentsStore.id)
        }
        input(id = argumentsStore.id, baseClass = "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500") {
            value(argumentsStore.data)
            placeholder("Arguments separated by `,`")
            changes.values() handledBy argumentsStore.update
        }
    }
}

private fun RenderContext.userSelector(userStore: Store<Participant?>, state: GlobalState) {
    label("block mb-2 text-sm font-medium text-gray-100") {
        + "User"
        `for`(userStore.id)
    }
    select(id = userStore.id, baseClass = "bg-gray-50 border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500") {
        state.players.keys.forEach { player ->
            option {
                + player.displayName
                value(player.name)
            }
        }
        changes.values().mapNotNull { state.participantByName(it) } handledBy userStore.update
    }
}

private fun RenderContext.actionSelector(actionStore: Store<Int>) {
    val actions = mapOf(
        Action.Key.BoardMove to "Board Move",
        Action.Key.GameRoll to "Game Roll",
        Action.Key.GameSet to "Game Set",
        Action.Key.GameStatusChange to "Game Status Change",
        Action.Key.GameDrop to "Game Drop",
        Action.Key.ItemReceive to "Item Receive",
        Action.Key.ItemUse to "Item Use",
    )
    label("block mb-2 text-sm font-medium text-gray-100") {
        + "Action"
        `for`(actionStore.id)
    }
    select(id = actionStore.id, baseClass = "bg-gray-50 border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500") {
        actions.forEach { (actionId, name) ->
            option {
                + name
                value(actionId.toString())
            }
        }
        changes.values().mapNotNull { it.toIntOrNull() } handledBy actionStore.update
    }
}

private suspend fun sendAction(
    socketSession: Session,
    userStore: Store<Participant?>,
    actionStore: Store<Int>,
    argumentsStore: Store<String>,
) {
    val user = userStore.current ?: return
    val action = actionStore.current
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
    argumentsStore.update("")
}

fun RenderContext.renderActionsBoard(
    globalStateFlow: StateFlow<GlobalState?>,
    userStore: Store<Participant?>,
    actionStore: Store<Int>,
    argumentsStore: Store<String>,
    socketSession: Session,
) {
    renderFromState(globalStateFlow) { state ->
        div {
            userSelector(userStore, state)
        }
        div {
            actionSelector(actionStore)
        }
        div {
            argumentsInput(argumentsStore)
        }
        div {
            button("text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800") {
                +"Send"
                disabled(socketSession.isOpen.map { !it })
                clicks handledBy { sendAction(socketSession, userStore, actionStore, argumentsStore) }
            }
        }
    }
}
