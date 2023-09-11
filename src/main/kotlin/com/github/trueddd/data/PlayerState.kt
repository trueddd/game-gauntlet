package com.github.trueddd.data

import com.github.trueddd.data.items.WheelItem

data class PlayerState(
    val stepsCount: Int = 0,
    val boardMoveAvailable: Boolean = true,
    val position: Int = GlobalState.START_POSITION,
    val inventory: List<WheelItem.InventoryItem> = emptyList(),
    val effects: List<WheelItem.Effect> = emptyList(),
    val gameHistory: List<GameHistoryEntry> = emptyList(),
) {

    companion object {
        fun calculateStintIndex(position: Int): Int {
            return when {
                position == GlobalState.START_POSITION -> 0
                position.rem(GlobalState.STINT_SIZE) == 0 -> position / GlobalState.STINT_SIZE - 1
                else -> position / GlobalState.STINT_SIZE
            }
        }
    }

    val currentGame: GameHistoryEntry?
        get() = gameHistory.lastOrNull()

    val currentActiveGame: GameHistoryEntry?
        get() = currentGame?.takeUnless { it.status.isComplete }

    val stintIndex: Int
        get() = calculateStintIndex(position)
}
