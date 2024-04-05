package com.github.trueddd.actions

import com.github.trueddd.core.ItemRoller
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.items.*
import com.github.trueddd.utils.ActionCreationException
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a${Action.Key.ItemUse}")
data class ItemUse(
    @SerialName("ub")
    val usedBy: Participant,
    @SerialName("iu")
    val itemUid: String,
    @SerialName("ar")
    val arguments: List<String> = emptyList(),
) : Action(Key.ItemUse) {

    constructor(usedBy: Participant, itemUid: String, vararg arguments: String)
            : this(usedBy, itemUid, arguments.asList())

    constructor(usedBy: Participant, item: WheelItem, vararg arguments: String)
            : this(usedBy, item.uid, arguments.asList())

    @ActionGenerator
    class Generator : Action.Generator<ItemUse> {

        override val actionKey = Key.ItemUse

        override fun generate(participant: Participant, arguments: List<String>): ItemUse {
            val itemUid = arguments.firstOrNull()
                ?: throw ActionCreationException("Couldn't parse itemUid from arguments: `$arguments`")
            return ItemUse(participant, itemUid, arguments.drop(1))
        }
    }

    @ActionHandler(key = Key.ItemUse)
    class Handler(
        private val itemRoller: ItemRoller,
    ) : Action.Handler<ItemUse> {

        override suspend fun handle(action: ItemUse, currentState: GlobalState): GlobalState {
            val item = currentState.players[action.usedBy]?.inventory?.firstOrNull { it.uid == action.itemUid }
                ?: currentState.players[action.usedBy]?.pendingEvents?.firstOrNull { it.uid == action.itemUid }
                ?: return currentState
            return when (item) {
                is Plasticine -> item.transform(
                    user = action.usedBy,
                    globalState = currentState,
                    arguments = action.arguments,
                    factories = itemRoller.allItemsFactories
                )
                is WheelItem.PendingEvent -> item.use(action.usedBy, currentState, action.arguments)
                is WheelItem.InventoryItem -> item.use(action.usedBy, currentState, action.arguments)
                else -> throw StateModificationException(action, "ItemUse is undefined for this item")
            }
        }
    }
}
