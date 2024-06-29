package com.github.trueddd

import com.github.trueddd.actions.Action
import com.github.trueddd.core.EventGate
import com.github.trueddd.data.PlayerName
import com.github.trueddd.di.ActionIssueDateComponentHolder
import com.github.trueddd.di.TimedIssueDateManager
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

    protected fun getRandomPlayerName() = eventGate.stateHolder.participants.random().name
    protected fun getPlayerNames() = eventGate.stateHolder.participants.toList().map { it.name }

    protected fun stateOf(playerName: PlayerName) = eventGate.stateHolder.current.stateOf(playerName)
    protected fun effectsOf(playerName: PlayerName) = stateOf(playerName).effects
    protected fun pendingEventsOf(playerName: PlayerName) = stateOf(playerName).pendingEvents
    protected fun inventoryOf(playerName: PlayerName) = stateOf(playerName).inventory
    protected fun positionOf(playerName: PlayerName) = stateOf(playerName).position
    protected fun stintOf(playerName: PlayerName) = stateOf(playerName).stintIndex
    protected fun gamesOf(playerName: PlayerName) = eventGate.stateHolder.current.gamesOf(playerName)
    protected fun lastGameOf(playerName: PlayerName) = stateOf(playerName).currentGame
    protected fun historyOf(playerName: PlayerName) = eventGate.stateHolder.currentPlayersHistory[playerName]!!

    protected suspend fun handleAction(action: Action) = eventGate.eventManager.consumeAction(action)

    protected suspend fun makeMove(playerName: PlayerName) {
        eventGate.parseAndHandle("$playerName:${Action.Key.BoardMove}")
        eventGate.parseAndHandle("$playerName:${Action.Key.GameRoll}")
        eventGate.parseAndHandle("$playerName:${Action.Key.GameStatusChange}:1")
    }

    protected suspend fun makeMovesUntilFinish(playerName: PlayerName) {
        flow {
            while (currentCoroutineContext().isActive) {
                makeMove(playerName)
                emit(positionOf(playerName))
            }
        }
            .filterNotNull()
            .takeWhile { it < eventGate.stateHolder.current.boardLength }
            .collect()
    }

    protected fun setupTimedIssueDateManager() {
        ActionIssueDateComponentHolder.set(TimedIssueDateManager())
    }

    @BeforeEach
    open fun startEventGate() {
        setupTimedIssueDateManager()
        eventGate.startNoLoad()
    }

    @AfterEach
    fun stopEventGate() {
        eventGate.stop()
    }
}
