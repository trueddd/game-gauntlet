package com.github.trueddd.core

import com.github.trueddd.data.repository.GameStateRepository

class LocalEventHistoryHolder(
    actionHandlerRegistry: ActionHandlerRegistry,
    gameStateRepository: GameStateRepository,
) : BaseEventHistoryHolder(actionHandlerRegistry, gameStateRepository)
