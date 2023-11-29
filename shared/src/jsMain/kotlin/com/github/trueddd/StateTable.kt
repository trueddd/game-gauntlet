package com.github.trueddd

import com.github.trueddd.data.GlobalState
import dev.fritz2.core.RenderContext
import kotlinx.coroutines.flow.StateFlow

private const val TableHeadColumnStyles = "border border-slate-600"
private const val TableColumnStyles = "border border-slate-700"

fun RenderContext.renderState(globalStateFlow: StateFlow<GlobalState?>) {
    renderFromState(globalStateFlow) { state ->
        table("border-collapse border border-slate-500") {
            thead {
                tr {
                    th(TableHeadColumnStyles) {
                        a { + "Player" }
                    }
                    state.players.keys.forEach {
                        th(TableHeadColumnStyles) {
                            a { + it.name }
                        }
                    }
                }
            }
            tbody {
                tr {
                    td(TableColumnStyles) {
                        a { + "Position" }
                    }
                    state.players.values.forEach {
                        td(TableColumnStyles) {
                            a { + it.position.toString() }
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        a { + "Game" }
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            playerState.currentGame?.game?.name?.let { a { + it } }
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        a { + "Inventory" }
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            a { + playerState.inventory.joinToString { "(${it.name})[${it.uid}]" } }
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        a { + "Effects" }
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            a { + playerState.effects.joinToString { "(${it.name})[${it.uid}]" } }
                        }
                    }
                }
                tr {
                    td(TableColumnStyles) {
                        a { + "Pending events" }
                    }
                    state.players.values.forEach { playerState ->
                        td(TableColumnStyles) {
                            a { + playerState.pendingEvents.joinToString { "(${it.name})[${it.uid}]" } }
                        }
                    }
                }
            }
        }
    }
}
