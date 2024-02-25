package com.github.trueddd.core

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
    data class Action(val payload: String) : Command("do:$payload")

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
