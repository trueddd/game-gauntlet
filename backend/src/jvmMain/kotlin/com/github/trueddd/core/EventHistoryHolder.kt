package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.flow.Flow

interface EventHistoryHolder {

    /**
     * Flow of actions that emits new item every time new action has been handled
     */
    val actionsChannel: Flow<Action>

    /**
     * Returns history of already handled actions
     */
    suspend fun getActions(): List<Action>

    /**
     * Pushes new actions to history, and suspends until passed action gets handled
     */
    suspend fun pushEvent(action: Action)

    /**
     * Saves current game state and all previously handled actions to the storage
     */
    suspend fun save(globalState: GlobalState)

    /**
     * Returns previously saved game state
     */
    suspend fun load(): GlobalState

    /**
     * Resets previously handled action list
     */
    fun drop()
}
