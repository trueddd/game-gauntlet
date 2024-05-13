package com.github.trueddd.data.model

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayersHistory

data class LoadedGameState(
    val globalState: GlobalState,
    val playersHistory: PlayersHistory,
)
