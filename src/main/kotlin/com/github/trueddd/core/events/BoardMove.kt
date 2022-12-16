package com.github.trueddd.core.events

import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

@Serializable
data class BoardMove(
    val rolledBy: Participant,
    val diceValue: Int,
    val modifiers: Int,
) : Action(Keys.BoardMove) {

    companion object {
        private const val MIN = 1
        private const val MAX = 10
    }

    val finalValue = (diceValue + modifiers).coerceAtLeast(MIN).coerceAtMost(MAX)
}
