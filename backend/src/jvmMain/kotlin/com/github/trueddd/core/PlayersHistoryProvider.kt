package com.github.trueddd.core

import com.github.trueddd.data.PlayerTurnsHistory
import kotlinx.coroutines.flow.StateFlow

interface PlayersHistoryProvider {

    val playersTurnsStateFlow: StateFlow<PlayersHistory>

    val currentPlayersHistory: PlayersHistory
        get() = playersTurnsStateFlow.value

    fun updateHistory(block: PlayersHistory.() -> PlayersHistory)
}

typealias PlayersHistory = Map<String, PlayerTurnsHistory>
