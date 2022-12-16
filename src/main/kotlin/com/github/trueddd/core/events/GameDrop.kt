package com.github.trueddd.core.events

import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

@Serializable
data class GameDrop(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Keys.GameDrop)
