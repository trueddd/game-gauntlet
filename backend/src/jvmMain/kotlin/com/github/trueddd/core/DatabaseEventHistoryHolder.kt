package com.github.trueddd.core

import com.github.trueddd.data.repository.DatabaseGameStateRepository
import com.github.trueddd.data.repository.GameStateRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single(binds = [EventHistoryHolder::class])
class DatabaseEventHistoryHolder(
    actionHandlerRegistry: ActionHandlerRegistry,
    @Named(DatabaseGameStateRepository.TAG)
    gameStateRepository: GameStateRepository,
) : BaseEventHistoryHolder(actionHandlerRegistry, gameStateRepository)
