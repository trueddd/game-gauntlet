package com.github.trueddd

import dev.fritz2.core.render
import dev.fritz2.remote.body
import dev.fritz2.remote.websocket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val socket = websocket("ws://localhost:8102/state?verbal=0").connect()
    socket.messages.body
        .onEach { console.log(it) }
        .launchIn(GlobalScope)
    render("#target") {
        h1 { + "AGG2 page" }
        div("css") {
            + "Text"
        }
    }
}
