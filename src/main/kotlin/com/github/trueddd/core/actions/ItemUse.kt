package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.ActionCreationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.Serializable

@Serializable
data class ItemUse(
    val usedBy: Participant,
    val itemUid: String,
) : Action(Key.ItemUse) {

    @ActionGenerator
    class Generator : Action.Generator<ItemUse> {

        override val actionKey = Key.ItemUse

        override fun generate(participant: Participant, arguments: List<String>): ItemUse {
            val itemUid = arguments.firstOrNull()
                ?: throw ActionCreationException("Couldn't parse itemUid from arguments: `$arguments`")
            return ItemUse(participant, itemUid)
        }
    }

    @ActionHandler(key = Key.ItemUse)
    class Handler : Action.Handler<ItemUse> {

        override suspend fun handle(action: ItemUse, currentState: GlobalState): GlobalState {
            val item = currentState.players[action.usedBy]?.inventory
                ?.firstOrNull { it.uid == action.itemUid }
                ?: return currentState
            return item.use(action.usedBy, currentState)
        }
    }
}
