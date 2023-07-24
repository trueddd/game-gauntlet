package com.github.trueddd.core.history

import com.github.trueddd.core.ActionHandlerRegistry
import com.github.trueddd.core.actions.Action
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File
import java.util.*

@Single(binds = [EventHistoryHolder::class])
open class LocalEventHistoryHolder(
    private val actionHandlerRegistry: ActionHandlerRegistry,
) : EventHistoryHolder {

    protected open val saveLocation = ".\\src\\main\\resources\\history"

    protected open val overwrite = false

    private val historyHolderFile by lazy {
        File(saveLocation)
            .also { it.createNewFile() }
    }

    private val latestEvents = LinkedList<Action>()

    private val monitor = Mutex(locked = false)

    override suspend fun pushEvent(action: Action) {
        monitor.lock()
        latestEvents.push(action)
        monitor.unlock()
    }

    override suspend fun save() {
        println("Saving Global state")
        monitor.lock()
        val eventsToSave = latestEvents.toList()
        monitor.unlock()
        val encoded = eventsToSave
            .asReversed()
            .joinToString("\n", postfix = "\n") { Json.encodeToString(it) }
        withContext(Dispatchers.IO) {
            if (overwrite) {
                historyHolderFile.writeText(encoded)
            } else {
                historyHolderFile.appendText(encoded)
            }
        }
        println("Global state saved")
    }

    override suspend fun load(): GlobalState {
        val eventsContent = withContext(Dispatchers.IO) {
            historyHolderFile.readLines()
        }
        return withContext(Dispatchers.Default) {
            val events = eventsContent
                .filter { it.isNotBlank() }
                .map { Json.decodeFromString(Action.serializer(), it) }
            events.fold(GlobalState.default()) { state, action ->
                actionHandlerRegistry
                    .handlerOf(action)
                    ?.consume(action, state)
                    ?: state
            }
        }
    }
}