package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.items.WheelItem

/**
 * Command represents an action sent by client to server.
 */
sealed class Command(val value: String) {

    /**
     * Launches event handler with an empty state
     */
    data object Start : Command("start")

    /**
     * Saves current actions history
     */
    data object Save : Command("save")

    /**
     * Restores saved state
     */
    data object Restore : Command("load")

    /**
     * Reset current store
     */
    data object Reset : Command("reset")

    /**
     * Disconnects client from socket
     */
    data object Disconnect : Command("bye")

    /**
     * Game-related action (e.g. `throw a dice`)
     */
    data class Action(val payload: String) : Command("do:$payload") {
        companion object {
            fun itemReceive(player: Participant, itemId: WheelItem.Id) = buildString {
                append(player.name)
                append(":")
                append(com.github.trueddd.actions.Action.Key.ItemReceive)
                append(":")
                append(itemId.value)
            }.let { Action(it) }
            fun itemUse(player: Participant, item: WheelItem, arguments: List<String>) = buildString {
                append(player.name)
                append(":")
                append(com.github.trueddd.actions.Action.Key.ItemUse)
                append(":")
                append(item.uid)
                arguments.forEach { append(":$it") }
            }.let { Action(it) }
        }
    }

    companion object {
        fun parseCommand(input: String): Command? {
            return when {
                input.startsWith("do:") -> Action(input.removePrefix("do:"))
                input == "start" -> Start
                input == "save" -> Save
                input == "load" -> Restore
                input == "bye" -> Disconnect
                input == "reset" -> Reset
                else -> null
            }
        }
    }
}
