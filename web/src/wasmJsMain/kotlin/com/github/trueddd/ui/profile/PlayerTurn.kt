package com.github.trueddd.ui.profile

import com.github.trueddd.actions.Action
import com.github.trueddd.data.Game
import com.github.trueddd.data.GameHistoryEntry
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

data class PlayerTurn(
    val moveDateTime: LocalDateTime,
    val start: Int,
    val end: Int,
    val gameHistoryEntry: GameHistoryEntry?,
) {
    companion object {
        // todo: replace mock with actual data
        fun turnsFrom(
            player: Participant,
            globalState: GlobalState,
            actions: List<Action>
        ): List<PlayerTurn> {
            val today = Clock.System.now().toLocalDateTime(DefaultTimeZone)
            return listOf(
                PlayerTurn(
                    moveDateTime = today,
                    start = 12,
                    end = 14,
                    gameHistoryEntry = null
                ),
                PlayerTurn(
                    moveDateTime = today.toInstant(DefaultTimeZone)
                        .minus(3.hours)
                        .toLocalDateTime(DefaultTimeZone),
                    start = 8,
                    end = 12,
                    gameHistoryEntry = GameHistoryEntry(
                        game = Game(id = Game.Id(1), name = "The Elder Game", Game.Genre.Runner),
                        status = Game.Status.Finished,
                        comment = null
                    )
                ),
                PlayerTurn(
                    moveDateTime = today.toInstant(DefaultTimeZone)
                        .minus(4.hours)
                        .toLocalDateTime(DefaultTimeZone),
                    start = 2,
                    end = 8,
                    gameHistoryEntry = GameHistoryEntry(
                        game = Game(id = Game.Id(2), name = "Веселая ферма", Game.Genre.Business),
                        status = Game.Status.Finished,
                        comment = null
                    )
                ),
                PlayerTurn(
                    moveDateTime = today.toInstant(DefaultTimeZone)
                        .minus(1.days)
                        .toLocalDateTime(DefaultTimeZone),
                    start = 0,
                    end = 2,
                    gameHistoryEntry = GameHistoryEntry(
                        game = Game(id = Game.Id(3), name = "Супер Корова 3", Game.Genre.Runner),
                        status = Game.Status.Finished,
                        comment = null
                    )
                )
            )
        }
    }
}
