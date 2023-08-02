package com.github.trueddd.data

import com.github.trueddd.data.items.WheelItem

data class PlayerState(
    val stepsCount: Int = 0,
    val boardMoveAvailable: Boolean = true,
    val position: Int = 0,
    val inventory: List<WheelItem.InventoryItem> = emptyList(),
    val effects: List<WheelItem.Effect> = emptyList(),
    val gameHistory: List<GameHistoryEntry> = emptyList(),
) {

    val currentGame: GameHistoryEntry?
        get() = gameHistory.lastOrNull()

    val currentActiveGame: GameHistoryEntry?
        get() = currentGame?.takeUnless { it.status.isComplete }
}
