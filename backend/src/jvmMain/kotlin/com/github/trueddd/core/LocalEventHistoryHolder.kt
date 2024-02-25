package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.globalState
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

open class LocalEventHistoryHolder(
    private val actionHandlerRegistry: ActionHandlerRegistry,
) : EventHistoryHolder {

    companion object {
        private const val TAG = "EventHistoryHolder"
    }

    protected open val saveLocation = ".\\src\\jvmMain\\resources\\history"

    protected open val overwrite = false

    private val historyHolderFile by lazy {
        File(saveLocation)
            .also { it.createNewFile() }
    }

    private val latestEvents = LinkedList<Action>()

    private val monitor = Mutex(locked = false)

    override val actionsChannel = MutableSharedFlow<Action>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun getActions(): List<Action> {
        return monitor.withLock { latestEvents.toList() }
    }

    override suspend fun pushEvent(action: Action) {
        monitor.withLock { latestEvents.push(action) }
        actionsChannel.emit(action)
    }

    override suspend fun save(globalState: GlobalState) {
        Log.info(TAG, "Saving Global state")
        val eventsToSave = getActions()
        val mapLayout = Json.encodeToString(GameGenreDistribution.serializer(), globalState.gameGenreDistribution)
        val events = eventsToSave
            .asReversed()
            .joinToString("\n") { Json.encodeToString(it) }
        val text = buildString {
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
            val mapLayout = fileContent.first().let { Json.decodeFromString(GameGenreDistribution.serializer(), it) }
            val events = fileContent
                .filter { it.isNotBlank() }
                .drop(1)
                .map { Json.decodeFromString(Action.serializer(), it) }
            val initialState = globalState(genreDistribution = mapLayout)
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

    override fun drop() {
        latestEvents.clear()
    }
}