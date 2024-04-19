package com.github.trueddd.core

import com.github.trueddd.data.GameConfig
import com.github.trueddd.data.PlayersHistory
import com.github.trueddd.data.StateSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GameStateProvider {

    fun initialize()

    /**
     * Displays current socket connection state. Actual only for authorized users, because socket connection does not
     * get established for regular user.
     */
    val serverConnectionStateFlow: StateFlow<SocketState>

    /**
     * Returns current game config. Doesn't change during the game, so it can be cached.
     */
    val gameConfig: StateFlow<GameConfig?>

    /**
     * Returns current game state, updates only once per session for unauthorized users.
     * For authorized users it updates every time when game state changes.
     */
    val snapshotFlow: StateFlow<StateSnapshot?>

    fun playersHistoryFlow(): Flow<PlayersHistory?>
}
