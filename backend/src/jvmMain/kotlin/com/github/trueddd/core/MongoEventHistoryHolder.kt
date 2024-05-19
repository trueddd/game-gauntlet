package com.github.trueddd.core

import com.github.trueddd.data.repository.GameStateRepository
import com.github.trueddd.data.repository.MongoGameStateRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single(binds = [EventHistoryHolder::class])
class MongoEventHistoryHolder(
    actionHandlerRegistry: ActionHandlerRegistry,
    @Named(MongoGameStateRepository.TAG)
    gameStateRepository: GameStateRepository,
) : BaseEventHistoryHolder(actionHandlerRegistry, gameStateRepository)
