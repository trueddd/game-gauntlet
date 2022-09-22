package com.github.trueddd.core.handler

import com.github.trueddd.core.Action
import com.github.trueddd.core.ActionConsumer
import com.github.trueddd.data.GlobalState

class PlayerForwardMoveHandler : ActionConsumer<Action.DiceRoll.BoardMoveAhead> {

    override suspend fun consume(action: Action.DiceRoll.BoardMoveAhead, currentState: GlobalState): GlobalState {
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
