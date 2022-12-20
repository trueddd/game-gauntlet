package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.events.ItemReceive
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.items.InTimeEvent
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.ItemReceive)
class ItemReceiveHandler : ActionConsumer<ItemReceive> {

    override suspend fun consume(action: ItemReceive, currentState: GlobalState): GlobalState {
        if (action.item is InTimeEvent) {
            return action.item.invoke(currentState, action.receivedBy)
        }
        return currentState.copy(
            players = currentState.players.mapValues { (participant, state) ->
                if (participant == action.receivedBy) state.copy(inventory = state.inventory + action.item) else state
            }
        )
    }
}
