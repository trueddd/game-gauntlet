package com.github.trueddd.core.history

import com.github.trueddd.core.ActionHandlerRegistry
import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.LinkedList

class LocalEventHistoryHolder(
    private val actionHandlerRegistry: ActionHandlerRegistry,
) : EventHistoryHolder {

    private val historyHolderFile by lazy {
        File(".\\src\\main\\resources\\history")
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
        withContext(Dispatchers.IO) {
            historyHolderFile.appendText(
                eventsToSave
                    .asReversed()
                    .joinToString("\n", postfix = "\n") { Json.encodeToString(it) }
            )
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
            var initialState = GlobalState.default()
            events.forEach {
                val handler = actionHandlerRegistry.handlerOf(it) ?: return@forEach
                initialState = handler.consume(it, initialState)
            }
            initialState
        }
    }
}