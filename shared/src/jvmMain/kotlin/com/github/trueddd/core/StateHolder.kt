package com.github.trueddd.core

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class StateHolder : ParticipantProvider, ParticipantStateProvider {

    private val _globalStateFlow = MutableStateFlow(GlobalState.default())
    val globalStateFlow = _globalStateFlow.asStateFlow()

    val current: GlobalState
        get() = _globalStateFlow.value

    fun update(block: GlobalState.() -> GlobalState) = _globalStateFlow.update(block)

    override val participants: Set<Participant>
        get() = current.players.keys

    override fun get(name: String): Participant? {
        return current.players.keys.firstOrNull { it.name == name }
    }

    override fun get(participant: Participant): PlayerState {
        return current.players[participant]!!
    }
}
