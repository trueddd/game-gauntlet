package com.github.trueddd.core

import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.flow.StateFlow

interface StateHolder : ParticipantProvider, ParticipantStateProvider {

    val globalStateFlow: StateFlow<GlobalState>

    val current: GlobalState

    fun update(block: GlobalState.() -> GlobalState)
}
