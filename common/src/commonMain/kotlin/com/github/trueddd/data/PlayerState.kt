package com.github.trueddd.data

import com.github.trueddd.items.DiceRollModifier
import com.github.trueddd.items.WheelItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property stepsCount amount of steps that have been made by player using [BoardMove][com.github.trueddd.actions.BoardMove].
 * @property boardMoveAvailable flag determining whether the next [BoardMove][com.github.trueddd.actions.BoardMove] is available for player
 * @property position current position of player on the board.
 * 0 means the starting point of the board, player cannot reenter this spot.
 * @property inventory current set of inventory item of player available for them to use.
 * @property effects current set of effects that are applied to player.
 * @property pendingEvents current set of events that have been received be player and now awaiting to be used.
 * @property currentGame current game that player is playing. Can be null if player has not rolled any game yet.
 * For more detailed info see [gameHistory][com.github.trueddd.data.GlobalState.gameHistory].
 */
@Serializable
data class PlayerState(
    @SerialName("sc")
    val stepsCount: Int = 0,
    @SerialName("ba")
    val boardMoveAvailable: Boolean = true,
    @SerialName("po")
    val position: Int = GlobalState.START_POSITION,
    @SerialName("in")
    val inventory: List<WheelItem.InventoryItem> = emptyList(),
    @SerialName("ef")
    val effects: List<WheelItem.Effect> = emptyList(),
    @SerialName("cg")
    val currentGame: GameHistoryEntry? = null,
    @SerialName("pe")
    val pendingEvents: List<WheelItem.PendingEvent> = emptyList(),
) {

    companion object {
        fun default() = PlayerState(
            stepsCount = 0,
            boardMoveAvailable = true,
            position = GlobalState.START_POSITION,
            inventory = emptyList(),
            effects = emptyList(),
            pendingEvents = emptyList(),
        )
        fun calculateStintIndex(position: Int): Int {
            return when {
                position == GlobalState.START_POSITION -> 0
                position.rem(GlobalState.STINT_SIZE) == 0 -> position / GlobalState.STINT_SIZE - 1
                else -> position / GlobalState.STINT_SIZE
            }
        }
    }

    val currentActiveGame: GameHistoryEntry?
        get() = currentGame?.takeIf { it.status == Game.Status.InProgress }

    val hasCurrentActive: Boolean
        get() = currentActiveGame != null

    val stintIndex: Int
        get() = calculateStintIndex(position)

    val modifiersSum: Int
        get() = effects.filterIsInstance<DiceRollModifier>().sumOf { it.modifier }

    val wheelItems: List<WheelItem>
        get() = inventory + effects + pendingEvents
}

inline fun <reified T : WheelItem.Effect> List<WheelItem.Effect>.without(): List<WheelItem.Effect> {
    return filter { it !is T }
}

fun <T : WheelItem> List<T>.without(itemUid: String): List<T> {
    return filter { it.uid != itemUid }
}
