package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayersHistory
import com.github.trueddd.data.globalState

/**
 * Event manager is a main component of the system.
 * Flow of handling game events:
 * 1. Parsing input commands and generating an action out of input with an [action generator][com.github.trueddd.actions.Action.Generator].
 * 2. Event manager looks up the [handler][com.github.trueddd.actions.Action.Handler] for the generated action.
 * 3. Found handler mutates the current state.
 * 4. Applied action is recorded to the [event history holder][com.github.trueddd.core.EventHistoryHolder],
 * so whole state can be recreated later after server reboot.
 */
interface EventManager {

    data class HandledAction(
        val id: Int,
        val issuedAt: Long,
        val error: Exception? = null,
    ) {
        companion object {
            fun from(action: Action, error: Exception? = null): HandledAction {
                return HandledAction(action.id, action.issuedAt, error)
            }
        }
    }

    suspend fun consumeAction(action: Action): HandledAction

    fun stopHandling()

    fun startHandling(
        initState: GlobalState = globalState(),
        playersHistory: PlayersHistory = initState.defaultPlayersHistory()
    )
}
