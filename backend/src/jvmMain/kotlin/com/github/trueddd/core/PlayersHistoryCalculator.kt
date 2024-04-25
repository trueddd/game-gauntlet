package com.github.trueddd.core

import com.github.trueddd.actions.*
import com.github.trueddd.data.*

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
                    history.copy(turns = history.turns + turn)
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
                    history.copy(turns = history.turns.dropLast(1) + turn)
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
                    if (oldState.stateOf(action.setBy).hasCurrentActive) {
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
                    history.copy(turns = history.turns.dropLast(1) + turn)
                }
            }
            is ItemReceive -> currentHistory
            is ItemUse -> currentHistory
        }
    }
}
