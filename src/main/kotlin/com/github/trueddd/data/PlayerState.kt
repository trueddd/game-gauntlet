package com.github.trueddd.data

import com.github.trueddd.data.items.DiceRollModifier
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
        get() = gameHistory.lastOrNull { it.status != Game.Status.Next }

    val currentActiveGame: GameHistoryEntry?
        get() = currentGame?.takeIf { it.status == Game.Status.InProgress }

    val hasCurrentActive: Boolean
        get() = currentActiveGame != null

    val stintIndex: Int
        get() = calculateStintIndex(position)

    val modifiersSum: Int
        get() = effects.filterIsInstance<DiceRollModifier>().sumOf { it.modifier }
}
