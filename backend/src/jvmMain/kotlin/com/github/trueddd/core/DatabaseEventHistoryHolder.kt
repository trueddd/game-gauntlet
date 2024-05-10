package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.*
import com.github.trueddd.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Single(binds = [EventHistoryHolder::class])
class DatabaseEventHistoryHolder(
    private val actionHandlerRegistry: ActionHandlerRegistry,
) : BaseEventHistoryHolder() {

    companion object {
        private const val TAG = "DatabaseEventHistoryHolder"
    }

    private val database = Database.connect(
        url = Environment.DatabaseUrl,
        driver = "org.postgresql.Driver",
        user = Environment.DatabaseUser,
        password = Environment.DatabasePassword,
    ).apply {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(ActionsTable)
        }
    }

    override suspend fun save(globalState: GlobalState) {
        Log.info(TAG, "Saving Global state")
        val timeRange = "${globalState.startDate}:${globalState.endDate}"
        val eventsToSave = getActions()
        val mapLayout = serialization.encodeToString(
            GameGenreDistribution.serializer(),
            globalState.gameGenreDistribution
        )
        val radioCoverage = serialization.encodeToString(
            RadioCoverage.serializer(),
            globalState.radioCoverage
        )
        val events = eventsToSave
            .asReversed()
            .joinToString("\n") { serialization.encodeToString(it) }
        val text = buildString {
            appendLine("time:$timeRange")
            appendLine("map:$mapLayout")
            appendLine("radio:$radioCoverage")
            append(events)
        }
        val result = suspendedTransactionAsync(Dispatchers.IO, database) {
            ActionsTable.deleteAll()
            ActionsTable.batchInsert(text.lines()) {
                this[ActionsTable.value] = it
            }
        }.await()
        Log.info(TAG, "Global state saved; ${result.size} lines saved")
    }

    override suspend fun load(): LoadedGameState {
        mutex.lock()
        val records = suspendedTransactionAsync(Dispatchers.IO, database) {
            ActionsTable.selectAll().map { it[ActionsTable.value] }
        }.await()
        Log.info(TAG, "Records found: ${records.size}")
        if (records.isEmpty()) {
            mutex.unlock()
            val state = globalState()
            return LoadedGameState(state, state.defaultPlayersHistory())
        }
        val (start, end) = records.firstOrNull { it.startsWith("time:") }
            ?.removePrefix("time:")
            ?.split(":")
            ?.let { (start, end) -> start.toLong() to end.toLong() }
            ?: run {
                mutex.unlock()
                throw IllegalArgumentException("Error while parsing game time range")
            }
        val mapLayout = records.firstOrNull { it.startsWith("map:") }
            ?.removePrefix("map:")
            ?.let { serialization.decodeFromString(GameGenreDistribution.serializer(), it) }
            ?: run {
                mutex.unlock()
                throw IllegalStateException("Distribution must be read, but actions list is empty")
            }
        val radioCoverage = records.firstOrNull { it.startsWith("radio:") }
            ?.removePrefix("radio:")
            ?.let { serialization.decodeFromString(RadioCoverage.serializer(), it) }
            ?: RadioCoverage.generateRandom(GlobalState.PLAYABLE_BOARD_RANGE, GlobalState.STINT_SIZE)
        val events = withContext(Dispatchers.Default) {
            records.filter { it.isNotBlank() }
                .mapNotNull {
                    try {
                        serialization.decodeFromString(Action.serializer(), it)
                    } catch (_: Exception) {
                        null
                    }
                }
        }
        val initialState = globalState(
            genreDistribution = mapLayout,
            startDateTime = Instant.fromEpochMilliseconds(start).toLocalDateTime(DefaultTimeZone),
            activePeriod = (end - start).toDuration(DurationUnit.MILLISECONDS),
            radioCoverage = radioCoverage,
        )
        var playersHistory = initialState.defaultPlayersHistory()
        val globalState = events.fold(initialState) { state, action ->
            val handler = actionHandlerRegistry.handlerOf(action) ?: return@fold state
            latestEvents.push(action)
            try {
                val newState = handler.handle(action, state)
                playersHistory = PlayersHistoryCalculator.calculate(
                    currentHistory = playersHistory,
                    action = action,
                    oldState = state,
                    newState = newState
                )
                newState
            } catch (error: StateModificationException) {
                Log.error(TAG, "Error caught while restoring state at action: $action")
                Log.error(TAG, "Current state: $state")
                error.printStackTrace()
                state
            }
        }
        mutex.unlock()
        return LoadedGameState(globalState, playersHistory)
    }
}
