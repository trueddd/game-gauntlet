package com.github.trueddd.core

import com.github.trueddd.data.Participant
import java.util.Date

sealed class Action(
    open val id: Int,
    val issuedAt: Date = Date(),
) {

    sealed class DiceRoll(
        override val id: Int,
        open val rolledBy: Participant,
        open val diceValue: Int,
        open val modifiers: Int,
    ) : Action(id = 1) {

        data class BoardMoveAhead(
            override val rolledBy: Participant,
            override val diceValue: Int,
            override val modifiers: Int,
        ) : DiceRoll(id = 2, rolledBy, diceValue, modifiers)

        data class GameDrop(
            override val rolledBy: Participant,
            override val diceValue: Int,
        ) : DiceRoll(id = 3, rolledBy, diceValue, modifiers = 0)
    }
}
