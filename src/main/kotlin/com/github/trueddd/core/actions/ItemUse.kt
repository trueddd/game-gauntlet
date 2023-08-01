package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.ActionGeneratorCreationException
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class ItemUse(
    val usedBy: Participant,
    val itemUid: String,
) : Action(Key.ItemUse) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator : Action.Generator<ItemUse> {

        override val actionKey = Key.ItemUse

        override fun generate(userName: String, arguments: List<String>): ItemUse {
            val itemUid = arguments.firstOrNull()
                ?: throw ActionGeneratorCreationException("Couldn't parse itemUid from arguments: `$arguments`")
            return ItemUse(Participant(userName), itemUid)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Key.ItemUse)
    class Handler : Action.Handler<ItemUse> {

        override suspend fun handle(action: ItemUse, currentState: GlobalState): GlobalState {
            val item = currentState.players[action.usedBy]?.inventory
                ?.firstOrNull { it.uid == action.itemUid }
                ?: return currentState
            return item.use(action.usedBy, currentState)
        }
    }
}
