package com.github.trueddd.core.history

import com.github.trueddd.core.ActionHandlerRegistry
import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File
import java.util.LinkedList

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

    private val monitor = Semaphore(1)

    override suspend fun pushEvent(action: Action) {
        monitor.acquire()
        latestEvents.push(action)
        monitor.release()
    }

    override suspend fun save() {
        println("Saving Global state")
        monitor.acquire()
        val eventsToSave = latestEvents.toList()
        monitor.release()
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