package com.github.trueddd.data

import com.github.trueddd.items.BoardTrap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StateSnapshot(
    @SerialName("ps")
    val playersState: Map<PlayerName, PlayerState>,
    @SerialName("bt")
    val boardTraps: Map<Int, BoardTrap>,
    @SerialName("wi")
    val winner: PlayerName?,
    @SerialName("or")
    val overallAmountOfPointsRaised: Long,
)
