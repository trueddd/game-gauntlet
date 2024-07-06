package com.github.trueddd.data.model.save

import com.github.trueddd.actions.Action
import kotlinx.serialization.Serializable

@Serializable
class ActionsConfig(
    val actions: List<Action>,
)
