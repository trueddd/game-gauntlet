package com.github.trueddd.core

import com.github.trueddd.data.Rollable
import com.github.trueddd.ui.wheels.DiceValue
import com.github.trueddd.ui.wheels.WheelState
import com.github.trueddd.ui.wheels.WheelType
import kotlinx.browser.window

class AppStorage {

    private fun wheelKeyHash(type: WheelType) = "wheel_${type}_hash"
    private fun wheelKeyRolled(type: WheelType) = "wheel_${type}_rolled"

    private fun List<Rollable>.hash(): Int {
        return map { it.name }.hashCode()
    }

    fun saveWheelItemsState(wheelState: WheelState) {
        window.localStorage.setItem(
            wheelKeyHash(wheelState.type),
            "${wheelState.items.hash()}"
        )
        val value = if (wheelState.type == WheelType.Dice) {
            wheelState.targetPosition
        } else {
            wheelState.targetPosition.rem(wheelState.items.size)
        }
        window.localStorage.setItem(wheelKeyRolled(wheelState.type), "$value")
    }

    fun getSavedDiceValue() = getSavedWheelState(DiceValue.All, WheelType.Dice)
        .targetPosition.takeIf { it > 0 } ?: 1

    fun getSavedWheelState(currentItemsList: List<Rollable>, type: WheelType): WheelState {
        val savedListHash = window.localStorage.getItem(wheelKeyHash(type))?.toIntOrNull()
            ?: return WheelState.default(currentItemsList, type)
        val savedRolledItemIndex = window.localStorage.getItem(wheelKeyRolled(type))?.toIntOrNull()
            ?: return WheelState.default(currentItemsList, type)
        return if (currentItemsList.hash() == savedListHash) {
            when (type) {
                WheelType.Dice -> WheelState.default(currentItemsList, type).copy(
                    targetPosition = savedRolledItemIndex,
                    rolledItem = DiceValue(savedRolledItemIndex),
                )
                else -> WheelState.default(currentItemsList, type).copy(
                    targetPosition = savedRolledItemIndex,
                    rolledItem = currentItemsList.getOrNull(savedRolledItemIndex),
                )
            }
        } else {
            WheelState.default(currentItemsList, type)
        }
    }
}
