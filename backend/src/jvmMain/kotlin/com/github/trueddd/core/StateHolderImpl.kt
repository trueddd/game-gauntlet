package com.github.trueddd.core

import com.github.trueddd.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single(binds = [
    StateHolder::class,
    ParticipantProvider::class,
    ParticipantStateProvider::class,
    PlayersHistoryProvider::class,
    CommunityFundRaisingTracker::class,
])
class StateHolderImpl : StateHolder {

    private val _globalStateFlow = MutableStateFlow(globalState())
    override val globalStateFlow = _globalStateFlow.asStateFlow()

    override val current: GlobalState
        get() = _globalStateFlow.value

    override fun update(block: GlobalState.() -> GlobalState) = _globalStateFlow.update(block)

    override val participants: Set<Participant>
        get() = current.players.toSet()

    override fun get(name: String): Participant? {
        return current.players.firstOrNull { it.name == name }
    }

    override fun get(participant: Participant): PlayerState {
        return current.stateSnapshot.playersState[participant.name]!!
    }

    private val _playersTurnsStateFlow: MutableStateFlow<PlayersHistory> = MutableStateFlow(
        value = current.defaultPlayersHistory()
    )

    override val playersTurnsStateFlow: StateFlow<PlayersHistory>
        get() = _playersTurnsStateFlow.asStateFlow()

    override fun updateHistory(block: PlayersHistory.() -> PlayersHistory) {
        _playersTurnsStateFlow.update(block)
    }

    override val overallAmountRaised: Long
        get() = current.stateSnapshot.overallAmountOfPointsRaised

    override val raisedAmountOnCurrentStage: Long
        get() = current.stateSnapshot.overallAmountOfPointsRaised.rem(CommunityFundRaisingTracker.SINGLE_EVENT_CAP)

    override val currentStage: Int
        get() = (current.stateSnapshot.overallAmountOfPointsRaised / CommunityFundRaisingTracker.SINGLE_EVENT_CAP).toInt()

    override fun updateOverallAmountRaised(amount: Long) {
        update { copy(stateSnapshot = stateSnapshot.copy(overallAmountOfPointsRaised = amount)) }
    }
}
