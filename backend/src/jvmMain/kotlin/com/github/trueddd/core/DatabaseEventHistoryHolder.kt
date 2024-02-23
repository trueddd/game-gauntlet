package com.github.trueddd.core

import com.github.trueddd.actions.Action
import com.github.trueddd.data.ActionsTable
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.globalState
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.StateModificationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import java.util.*

@Single(binds = [EventHistoryHolder::class])
class DatabaseEventHistoryHolder(
    private val actionHandlerRegistry: ActionHandlerRegistry,
) : EventHistoryHolder {

    companion object {
        private const val TAG = "DatabaseEventHistoryHolder"
    }

    private val latestEvents = LinkedList<Action>()

    override val actionsChannel = Channel<Action>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val monitor = Mutex(locked = false)

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

    override suspend fun getActions(): List<Action> {
        return monitor.withLock { latestEvents.toList() }
    }

    override suspend fun pushEvent(action: Action) {
        monitor.withLock { latestEvents.push(action) }
        actionsChannel.send(action)
    }

    override suspend fun save(globalState: GlobalState) {
        Log.info(TAG, "Saving Global state")
        val eventsToSave = getActions()
        val mapLayout = Json.encodeToString(
            GameGenreDistribution.serializer(),
            globalState.gameGenreDistribution
        )
        val events = eventsToSave
            .asReversed()
            .joinToString("\n") { Json.encodeToString(it) }
        val text = buildString {
            appendLine(mapLayout)
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

    override suspend fun load(): GlobalState {
        monitor.lock()
        val records = suspendedTransactionAsync(Dispatchers.IO, database) {
            ActionsTable.selectAll().map { it[ActionsTable.value] }
        }.await()
        Log.info(TAG, "Records found: ${records.size}")
        if (records.isEmpty()) {
            return globalState()
        }
        val mapLayout = records.firstOrNull()?.let {
            Json.decodeFromString(GameGenreDistribution.serializer(), it)
        } ?: throw IllegalStateException("Distribution must be read, but actions list is empty")
        val eventsContent = records.drop(1)
            .filter { it.isNotBlank() }
        val events = withContext(Dispatchers.Default) {
            eventsContent.map { Json.decodeFromString(Action.serializer(), it) }
        }
        val initialState = globalState(genreDistribution = mapLayout)
        return events.fold(initialState) { state, action ->
            val handler = actionHandlerRegistry.handlerOf(action) ?: return@fold state
            latestEvents.push(action)
            try {
                handler.handle(action, state)
            } catch (error: StateModificationException) {
                Log.error(TAG, "Error caught while restoring state at action: $action")
                Log.error(TAG, "Current state: $state")
                error.printStackTrace()
                state
            }
        }.also { monitor.unlock() }
    }

    override fun drop() {
        latestEvents.clear()
    }
}
