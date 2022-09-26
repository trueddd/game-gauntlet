package com.github.trueddd.core.events

import com.github.trueddd.data.Participant
import com.github.trueddd.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
sealed class Action(
    open val id: Int,
    @Serializable(with = DateSerializer::class)
    val issuedAt: Date = Date(),
) {

    object Keys {
        const val BoardMove = 1
        const val GameDrop = 2
    }

    @Serializable
    data class BoardMove(
        val rolledBy: Participant,
        val diceValue: Int,
        val modifiers: Int,
    ) : Action(Keys.BoardMove)

    @Serializable
    data class GameDrop(
        val rolledBy: Participant,
        val diceValue: Int,
    ) : Action(Keys.GameDrop)
}
