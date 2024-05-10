package com.github.trueddd.core

import com.github.trueddd.actions.*
import com.github.trueddd.data.*
import com.github.trueddd.items.DontWannaPlayThis

object PlayersHistoryCalculator {

    private fun PlayersHistory.update(
        playerName: String,
        block: (PlayerTurnsHistory) -> PlayerTurnsHistory
    ): PlayersHistory {
        return mapValues { (key, value) ->
            if (key == playerName) {
                block(value)
            } else {
                value
            }
        }
    }

    fun calculate(
        currentHistory: PlayersHistory,
        action: Action,
        oldState: GlobalState,
        newState: GlobalState,
    ): PlayersHistory {
        return when (action) {
            is BoardMove -> {
                currentHistory.update(action.rolledBy.name) { history ->
                    val turn = Turn(
                        moveDate = action.issuedAt,
                        moveRange = oldState.positionOf(action.rolledBy) .. newState.positionOf(action.rolledBy),
                        game = newState.stateOf(action.rolledBy).currentGame,
                    )
                    history.copy(
                        turns = history.turns + turn,
                        statistics = history.statistics.copy(
                            thrownDices = history.statistics.thrownDices + action.diceValue
                        )
                    )
                }
            }
            is GameDrop -> {
                currentHistory.update(action.rolledBy.name) { history ->
                    val turn = history.turns.last().copy(
                        moveRange = history.turns.last().moveRange?.first
                            ?.let { it .. newState.positionOf(action.rolledBy) }
                            ?: oldState.positionOf(action.rolledBy) .. newState.positionOf(action.rolledBy),
                        game = newState.stateOf(action.rolledBy).currentGame,
                    )
                    history.copy(
                        turns = history.turns.dropLast(1) + turn,
                        statistics = history.statistics.copy(
                            droppedGames = history.statistics.droppedGames + 1
                        )
                    )
                }
            }
            is GameRoll -> {
                currentHistory.update(action.participant.name) { history ->
                    if (history.turns.lastOrNull()?.game?.status == Game.Status.Dropped) {
                        val turn = history.turns.last().copy(
                            moveDate = action.issuedAt,
                            moveRange = null,
                            game = newState.stateOf(action.participant).currentGame,
                        )
                        history.copy(turns = history.turns + turn)
                    } else {
                        val turn = history.turns.last().copy(
                            game = newState.stateOf(action.participant).currentGame,
                        )
                        history.copy(turns = history.turns.dropLast(1) + turn)
                    }
                }
            }
            is GameSet -> {
                currentHistory.update(action.setBy.name) { history ->
                    if (oldState.stateOf(action.setBy).hasCurrentActiveGame) {
                        val turn = Turn(
                            moveDate = action.issuedAt,
                            moveRange = null,
                            game = newState.gamesOf(action.setBy).lastOrNull(),
                        )
                        history.copy(turns = history.turns + turn)
                    } else {
                        val turn = history.turns.lastOrNull()?.copy(
                            game = newState.stateOf(action.setBy).currentGame,
                        ) ?: Turn(
                            moveDate = action.issuedAt,
                            moveRange = oldState.positionOf(action.setBy) .. newState.positionOf(action.setBy),
                            game = newState.stateOf(action.setBy).currentGame,
                        )
                        history.copy(turns = history.turns.dropLast(1) + turn)
                    }
                }
            }
            is GameStatusChange -> {
                currentHistory.update(action.participant.name) { history ->
                    val turn = history.turns.last().copy(
                        game = newState.stateOf(action.participant).currentGame,
                    )
                    history.copy(
                        turns = history.turns.dropLast(1) + turn,
                        statistics = history.statistics.copy(
                            finishedGames = when (action.gameNewStatus) {
                                Game.Status.Finished -> history.statistics.finishedGames + 1
                                else -> history.statistics.finishedGames
                            },
                            rerolledGames = when (action.gameNewStatus) {
                                Game.Status.Rerolled -> history.statistics.rerolledGames + 1
                                else -> history.statistics.rerolledGames
                            }
                        )
                    )
                }
            }
            is ItemReceive -> currentHistory
            is ItemUse -> {
                val usedItem = oldState.stateOf(action.usedBy).wheelItems.firstOrNull { it.uid == action.itemUid }
                    ?: return currentHistory
                when (usedItem) {
                    is DontWannaPlayThis -> currentHistory.update(action.usedBy.name) { history ->
                        history.copy(
                            statistics = history.statistics.copy(
                                rerolledGames = history.statistics.rerolledGames + 1
                            )
                        )
                    }
                    else -> currentHistory
                }
            }
        }
    }
}
