package com.github.trueddd.core.actions

import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

@Serializable
data class GameDrop(
    val rolledBy: Participant,
    val diceValue: Int,
) : Action(Keys.GameDrop)
