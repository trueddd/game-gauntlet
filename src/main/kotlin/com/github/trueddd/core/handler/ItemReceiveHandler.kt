package com.github.trueddd.core.handler

import com.github.trueddd.core.actions.Action
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.items.WheelItem
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.ItemReceive)
class ItemReceiveHandler : ActionConsumer<ItemReceive> {

    override suspend fun consume(action: ItemReceive, currentState: GlobalState): GlobalState {
        return when (action.item) {
            is WheelItem.InventoryItem -> currentState.updatePlayer(action.receivedBy) {
                it.copy(inventory = it.inventory + action.item)
            }
            is WheelItem.Effect -> currentState.updatePlayer(action.receivedBy) {
                it.copy(effects = it.effects + action.item)
            }
            is WheelItem.Event -> action.item.invoke(currentState, action.receivedBy)
        }
    }
}
