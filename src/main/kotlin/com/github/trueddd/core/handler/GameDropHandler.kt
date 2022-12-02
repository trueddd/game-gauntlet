package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.GameDrop)
class GameDropHandler : ActionConsumer<Action.GameDrop> {

    override suspend fun consume(action: Action.GameDrop, currentState: GlobalState): GlobalState {
        val newPlayersState = currentState.players
            .mapValues { (participant, playerState) ->
                if (action.rolledBy == participant) {
                    playerState.copy(position = playerState.position - action.diceValue)
                } else {
                    playerState
                }
            }
        return currentState.copy(players = newPlayersState)
    }
}
