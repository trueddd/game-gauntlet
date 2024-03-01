package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.globalState
import com.github.trueddd.utils.DefaultTimeZone
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

open class LocalEventHistoryHolder(
    private val actionHandlerRegistry: ActionHandlerRegistry,
) : BaseEventHistoryHolder() {

    companion object {
        private const val TAG = "EventHistoryHolder"
    }

    protected open val saveLocation = ".\\src\\jvmMain\\resources\\history"

    protected open val overwrite = false

    private val historyHolderFile by lazy {
        File(saveLocation)
            .also { it.createNewFile() }
    }

    override suspend fun save(globalState: GlobalState) {
        Log.info(TAG, "Saving Global state")
        val eventsToSave = getActions()
        val timeRange = "${globalState.startDate}:${globalState.endDate}"
        val mapLayout = Json.encodeToString(GameGenreDistribution.serializer(), globalState.gameGenreDistribution)
        val events = eventsToSave
            .asReversed()
            .joinToString("\n") { Json.encodeToString(it) }
        val text = buildString {
            appendLine(timeRange)
            appendLine(mapLayout)
            appendLine(events)
        }
        withContext(Dispatchers.IO) {
            if (overwrite) {
                historyHolderFile.writeText(text)
            } else {
                historyHolderFile.appendText(text)
            }
        }
        Log.info(TAG, "Global state saved")
    }

    override suspend fun load(): GlobalState {
        val fileContent = withContext(Dispatchers.IO) {
            historyHolderFile.readLines()
        }
        return withContext(Dispatchers.Default) {
            val (start, end) = fileContent.getOrNull(0)
                ?.split(":")
                ?.let { (start, end) -> start.toLong() to end.toLong() }
                ?: throw IllegalArgumentException("Error while parsing game time range")
            val mapLayout = fileContent.getOrNull(1)
                ?.let { Json.decodeFromString(GameGenreDistribution.serializer(), it) }
                ?: throw IllegalArgumentException("Error while parsing map layout")
            val events = fileContent
                .drop(2)
                .filter { it.isNotBlank() }
                .map { Json.decodeFromString(Action.serializer(), it) }
            val initialState = globalState(
                genreDistribution = mapLayout,
                startDateTime = Instant.fromEpochMilliseconds(start).toLocalDateTime(DefaultTimeZone),
                activePeriod = (end - start).toDuration(DurationUnit.MILLISECONDS),
            )
            events.fold(initialState) { state, action ->
                val handler = actionHandlerRegistry.handlerOf(action) ?: return@fold state
                try {
                    handler.handle(action, state)
                } catch (error: StateModificationException) {
                    Log.error(TAG, "Error caught while restoring state at action: $action")
                    Log.error(TAG, "Current state: $state")
                    error.printStackTrace()
                    state
                }
            }
        }
    }
}
