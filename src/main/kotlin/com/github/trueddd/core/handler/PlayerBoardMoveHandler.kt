package com.github.trueddd.core.handler

import com.github.trueddd.core.actions.Action
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.coerceDiceValue
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.BoardMove)
class PlayerBoardMoveHandler : ActionConsumer<BoardMove> {

    override suspend fun consume(action: BoardMove, currentState: GlobalState): GlobalState {
        val newState = currentState.updatePlayer(action.rolledBy) { playerState ->
            val moveValue = coerceDiceValue(action.diceValue + playerState.diceModifier)
            val finalPosition = minOf(playerState.position + moveValue, currentState.boardLength)
            playerState.copy(position = finalPosition)
        }
        val winner = newState.players.entries
            .firstOrNull { (_, state) -> state.position == currentState.boardLength }
            ?.key
        return newState.copy(
            winner = currentState.winner ?: winner,
        )
    }
}
