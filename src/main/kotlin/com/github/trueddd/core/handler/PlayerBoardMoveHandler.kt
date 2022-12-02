package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.BoardMove)
class PlayerBoardMoveHandler : ActionConsumer<Action.BoardMove> {

    override suspend fun consume(action: Action.BoardMove, currentState: GlobalState): GlobalState {
        val newPlayersState = currentState.players
            .mapValues { (participant, playerState) ->
                if (action.rolledBy == participant) {
                    playerState.copy(position = playerState.position + action.diceValue + action.modifiers)
                } else {
                    playerState
                }
            }
        return currentState.copy(players = newPlayersState)
    }
}
