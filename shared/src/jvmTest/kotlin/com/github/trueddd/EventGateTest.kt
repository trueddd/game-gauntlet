package com.github.trueddd

import com.github.trueddd.core.EventGate
import com.github.trueddd.actions.Action
import com.github.trueddd.data.Participant
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class EventGateTest {

    protected val eventGate: EventGate = provideEventGate()
    protected val genreDistribution
        get() = eventGate.stateHolder.current.gameGenreDistribution

    protected fun requireRandomParticipant() = eventGate.stateHolder.participants.random()
    protected fun requireParticipants() = eventGate.stateHolder.participants.toList()

    protected fun stateOf(participant: Participant) = eventGate.stateHolder.current.stateOf(participant)
    protected fun effectsOf(participant: Participant) = stateOf(participant).effects
    protected fun pendingEventsOf(participant: Participant) = stateOf(participant).pendingEvents
    protected fun inventoryOf(participant: Participant) = stateOf(participant).inventory
    protected fun positionOf(participant: Participant) = stateOf(participant).position
    protected fun lastGameOf(participant: Participant) = stateOf(participant).currentGame

    protected suspend fun handleAction(action: Action) = eventGate.eventManager.suspendConsumeAction(action)

    protected suspend fun makeMove(participant: Participant) {
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.BoardMove}")
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameRoll}")
        eventGate.parseAndHandleSuspend("${participant.name}:${Action.Key.GameStatusChange}:1")
    }

    protected suspend fun makeMovesUntilFinish(player: Participant) {
        flow {
            while (currentCoroutineContext().isActive) {
                makeMove(player)
                emit(positionOf(player))
            }
        }
            .filterNotNull()
            .takeWhile { it < eventGate.stateHolder.current.boardLength }
            .collect()
    }

    @BeforeEach
    fun startEventGate() {
        eventGate.start()
    }

    @AfterEach
    fun stopEventGate() {
        eventGate.stop()
    }
}
