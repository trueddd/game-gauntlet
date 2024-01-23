package com.github.trueddd

import com.github.trueddd.data.GlobalState
import com.github.trueddd.items.WheelItem
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import kotlinx.browser.window
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TableHeadColumnStyles = "border border-slate-600"
private const val TableColumnStyles = "border border-slate-700 text-center"
private const val TableWheelItemsContainerStyles = "flex flex-wrap flex-row justify-center p-1 gap-1"

private val WheelItem.color: String
    get() = when (this) {
        is WheelItem.Event -> "bg-cyan-500"
        is WheelItem.PendingEvent -> "bg-teal-500"
        is WheelItem.InventoryItem -> "bg-amber-500"
        is WheelItem.Effect.Buff -> "bg-green-500"
        is WheelItem.Effect.Debuff -> "bg-red-500"
    }

private fun RenderContext.text(value: String) = a("m-1") { + value }

private suspend fun String.copyToClipboard(): Result<String> {
    return suspendCoroutine { continuation ->
        window.navigator.clipboard.writeText(this)
            .then { continuation.resume(Result.success(this)) }
            .catch { continuation.resume(Result.failure(it)) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun HtmlTag<*>.copyByClicks(content: String) {
    clicks
        .mapLatest { content.copyToClipboard() }
        .handledBy {
            if (it.isSuccess) {
                console.log("Copied: ${it.getOrNull()}")
            } else {
                console.error("Copying error", it.exceptionOrNull())
            }
        }
}

private fun HtmlTag<*>.renderWheelItem(item: WheelItem) {
    div("${item.color} w-fit px-1 rounded cursor-pointer") {
        a("text-stone-50") { + item.name }
        copyByClicks(item.uid)
    }
}

fun RenderContext.renderState(globalStateFlow: StateFlow<GlobalState?>) {
    renderFromState(globalStateFlow) { state ->
        table("border-collapse border border-slate-500 text-slate-300") {
            thead {
                tr {
                    th(TableHeadColumnStyles) {
                        text("Player")
                    }
                    state.players.keys.forEach {
                        th(TableHeadColumnStyles) {
                            text(it.name)
                        }
                    }
                }
            }
            tbody {
                tr {
                    td(TableColumnStyles) {
                        text("Position")
                    }
                    state.players.values.forEach {
                        td(TableColumnStyles) {
                            text(it.position.toString())
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        text("Game")
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            val value = playerState.currentGame?.game?.name ?: "-"
                            text(value)
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        text("Inventory")
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            div(TableWheelItemsContainerStyles) {
                                playerState.inventory.forEach { renderWheelItem(it) }
                            }
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        text("Effects")
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            div(TableWheelItemsContainerStyles) {
                                playerState.effects.forEach { renderWheelItem(it) }
                            }
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        text("Pending events")
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            div(TableWheelItemsContainerStyles) {
                                playerState.pendingEvents.forEach { renderWheelItem(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}
