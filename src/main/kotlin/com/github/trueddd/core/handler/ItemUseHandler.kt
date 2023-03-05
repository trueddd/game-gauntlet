package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.events.ItemUse
import com.github.trueddd.data.GlobalState
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.ItemUse)
class ItemUseHandler : ActionConsumer<ItemUse> {

    override suspend fun consume(action: ItemUse, currentState: GlobalState): GlobalState {
        val item = currentState.players[action.usedBy]?.inventory
            ?.firstOrNull { it.uid == action.itemUid }
            ?: return currentState
        return item.use(action.usedBy, currentState)
    }
}
