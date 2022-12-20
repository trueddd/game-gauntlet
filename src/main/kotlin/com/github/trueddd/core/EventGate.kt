package com.github.trueddd.core

import com.github.trueddd.core.history.EventHistoryHolder
import org.koin.core.annotation.Single

@Single
class EventGate(
    val stateHolder: StateHolder,
    val inputParser: InputParser,
    val eventManager: EventManager,
    val historyHolder: EventHistoryHolder,
)
