package com.github.trueddd

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.WebEnvironment
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.render
import dev.fritz2.core.storeOf
import dev.fritz2.remote.body
import dev.fritz2.remote.websocket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val decoder = Json {
        allowStructuredMapKeys = true
    }
    val socket = websocket("ws://localhost:${WebEnvironment.ServerPort}/state").connect()
    val globalState = socket.messages.body
        .onEach { console.log(it) }
        .filterNot { it.startsWith("YOU") }
        .map { decoder.decodeFromString<GlobalState>(it) }
        .catch { console.error(it.message) }
        .stateIn(GlobalScope, SharingStarted.Eagerly, null)
    render("#target") {
        val user = storeOf<Participant?>(null)
        val action = storeOf<Int?>(null)
        val arguments = storeOf("")
        div("flex flex-row justify-around p-4 gap-4") {
            div("basis-1/5 flex flex-col gap-2") {
                renderActionsBoard(globalState, user, action, arguments, socket)
            }
            div("basis-3/5") {
                renderState(globalState)
            }
            div("basis-1/5") {
                renderArchives()
            }
        }
        renderMap(globalState)
    }
}

fun RenderContext.renderFromState(globalStateFlow: StateFlow<GlobalState?>, block: Tag<*>.(GlobalState) -> Unit) {
    globalStateFlow.renderNotNull { state ->
        block(state)
    }
}
