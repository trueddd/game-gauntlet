package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.data.GlobalState

class ItemReceiveHandler : ActionConsumer<Action.ItemReceive> {

    override suspend fun consume(action: Action.ItemReceive, currentState: GlobalState): GlobalState {
        val newPlayerState = currentState.players[action.receivedBy]
            ?.let { it.copy(inventory = it.inventory + action.item) }
            ?: return currentState
        return currentState.copy(
            players = currentState.players.mapValues { (participant, state) ->
                if (participant == action.receivedBy) newPlayerState else state
            }
        )
    }
}
