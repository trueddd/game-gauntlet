package com.github.trueddd.ui.wheels

import com.github.trueddd.data.Game

sealed class WheelType(val key: String) {
    data object Items : WheelType("items")
    data class Games(val genre: Game.Genre) : WheelType("games_${genre.ordinal}")
    data object Players : WheelType("players")
    data object Dice : WheelType("dice")
    data object Coin : WheelType("coin")
}
