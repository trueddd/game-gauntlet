package com.github.trueddd.data

import com.github.trueddd.items.DiceRollModifier
import com.github.trueddd.items.WheelItem
import kotlinx.serialization.Serializable

/**
 * @property stepsCount amount of steps that have been made by player using [BoardMove][com.github.trueddd.actions.BoardMove].
 * @property boardMoveAvailable flag determining whether the next [BoardMove][com.github.trueddd.actions.BoardMove] is available for player
 * @property position current position of player on the board.
 * 0 means the starting point of the board, player cannot reenter this spot.
 * @property inventory current set of inventory item of player available for them to use.
 * @property effects current set of effects that are applied to player.
 * @property gameHistory whole history of games (including current one) that have been played or rerolled by player.
 * @property pendingEvents current set of events that have been received be player and now awaiting to be used.
 */
@Serializable
data class PlayerState(
    val stepsCount: Int = 0,
    val boardMoveAvailable: Boolean = true,
    val position: Int = GlobalState.START_POSITION,
    val inventory: List<WheelItem.InventoryItem> = emptyList(),
    val effects: List<WheelItem.Effect> = emptyList(),
    val gameHistory: List<GameHistoryEntry> = emptyList(),
    val pendingEvents: List<WheelItem.PendingEvent> = emptyList(),
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

    fun updatedHistoryWithLast(block: (GameHistoryEntry) -> GameHistoryEntry): List<GameHistoryEntry> {
        return gameHistory.mapIndexed { index, entry -> if (index == gameHistory.size - 1) block(entry) else entry }
    }
}

inline fun <reified T : WheelItem.Effect> List<WheelItem.Effect>.without(): List<WheelItem.Effect> {
    return filter { it !is T }
}

fun <T : WheelItem> List<T>.without(itemUid: String): List<T> {
    return filter { it.uid != itemUid }
}
