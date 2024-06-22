package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.globalState
import com.github.trueddd.data.model.LoadedGameState
import com.github.trueddd.data.model.SavedState
import com.github.trueddd.data.repository.GameStateRepository
import com.github.trueddd.utils.DefaultTimeZone
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class EventHistoryHolderImpl(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val gameStateRepository: GameStateRepository,
) : EventHistoryHolder {

    companion object {
        private const val TAG = "BaseEventHistoryHolder"
    }

    private val mutex = Mutex(locked = false)

    private val latestEvents = LinkedList<Action>()

    override val actionsChannel = MutableSharedFlow<Action>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun getActions(): List<Action> {
        return mutex.withLock { latestEvents.toList() }
    }

    override suspend fun pushEvent(action: Action) {
        mutex.withLock { latestEvents.push(action) }
        actionsChannel.emit(action)
    }

    override suspend fun save(globalState: GlobalState) {
        Log.info(TAG, "Saving Global state")
        val eventsToSave = getActions()
        gameStateRepository.save(globalState, eventsToSave)
        Log.info(TAG, "Global state saved")
    }

    override suspend fun load(): LoadedGameState {
        return mutex.withLock {
            createStateFromSave(gameStateRepository.load())
        }
    }

    override fun drop() {
        latestEvents.clear()
    }

    private suspend fun createStateFromSave(savedState: SavedState): LoadedGameState {
        return when (savedState) {
            is SavedState.NoRecords -> {
                val state = globalState()
                LoadedGameState(state, state.defaultPlayersHistory())
            }
            is SavedState.MapLayoutParsingError -> {
                throw IllegalStateException("Distribution must be read")
            }
            is SavedState.TimeRangeParsingError -> {
                throw IllegalArgumentException("Error while parsing game time range")
            }
            is SavedState.Success -> {
                val initialState = globalState(
                    genreDistribution = savedState.mapLayout,
                    startDateTime = Instant.fromEpochMilliseconds(savedState.timeRange.first)
                        .toLocalDateTime(DefaultTimeZone),
                    activePeriod = (savedState.timeRange.last - savedState.timeRange.first)
                        .toDuration(DurationUnit.MILLISECONDS),
                    radioCoverage = savedState.radioCoverage,
                    raisedAmountOfPoints = savedState.pointsCollected,
                )
                var playersHistory = initialState.defaultPlayersHistory()
                val globalState = savedState.actions
                    .sortedBy { it.issuedAt }
                    .fold(initialState) { state, action ->
                        val handler = actionHandlerRegistry.handlerOf(action) ?: return@fold state
                        try {
                            val newState = handler.handle(action, state)
                            playersHistory = PlayersHistoryCalculator.calculate(
                                currentHistory = playersHistory,
                                action = action,
                                oldState = state,
                                newState = newState
                            )
                            latestEvents.push(action)
                            newState
                        } catch (error: StateModificationException) {
                            Log.error(TAG, "Failed to handle action: $action. Got message: ${error.message}")
                            state
                        }
                    }
                LoadedGameState(globalState, playersHistory)
            }
        }
    }
}
