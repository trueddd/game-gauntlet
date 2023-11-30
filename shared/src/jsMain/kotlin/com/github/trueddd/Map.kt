package com.github.trueddd

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import kotlinx.coroutines.flow.StateFlow
import org.w3c.dom.HTMLDivElement

fun RenderContext.renderMap(globalStateFlow: StateFlow<GlobalState?>) {
    renderFromState(globalStateFlow) { state ->
        div("flex flex-row") {
            genreLegend(Game.Genre.Runner)
            genreLegend(Game.Genre.ThreeInRow)
            genreLegend(Game.Genre.Shooter)
            genreLegend(Game.Genre.PointAndClick)
            genreLegend(Game.Genre.Business)
            genreLegend(Game.Genre.Puzzle)
            genreLegend(Game.Genre.Special)
        }
        div("flex flex-row flex-wrap") {
            mapCell(0, null)
            state.gameGenreDistribution.genres.forEachIndexed { index, cell ->
                mapCell(index + 1, cell)
            }
        }
    }
}

private fun HtmlTag<HTMLDivElement>.genreLegend(genre: Game.Genre) {
    a("p-2 ${genre.color}") {
        + genre.name
    }
}

private fun HtmlTag<HTMLDivElement>.mapCell(index: Int, cell: Game.Genre?) {
    a("box-border h-12 w-12 p-4 border-4 flex justify-center items-center ${cell?.color ?: "bg-emerald-50"}") {
        + "$index"
    }
}

private val Game.Genre.color: String
    get() = when (this) {
        Game.Genre.Runner -> "bg-red-400"
        Game.Genre.Business -> "bg-blue-400"
        Game.Genre.Puzzle -> "bg-violet-400"
        Game.Genre.PointAndClick -> "bg-green-400"
        Game.Genre.Shooter -> "bg-amber-400"
        Game.Genre.ThreeInRow -> "bg-orange-400"
        Game.Genre.Special -> "bg-gray-400"
    }
