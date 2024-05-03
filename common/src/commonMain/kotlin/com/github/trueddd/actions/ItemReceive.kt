package com.github.trueddd.actions

import com.github.trueddd.core.ItemRoller
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.items.*
import com.github.trueddd.utils.StateModificationException
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a${Action.Key.ItemReceive}")
data class ItemReceive(
    @SerialName("rb")
    val receivedBy: Participant,
    @SerialName("wi")
    val item: WheelItem,
) : Action(Key.ItemReceive) {

    @ActionGenerator
    class Generator(private val itemRoller: ItemRoller) : Action.Generator<ItemReceive> {

        override val actionKey = Key.ItemReceive

        override fun generate(participant: Participant, arguments: List<String>): ItemReceive {
            val item = arguments.firstOrNull()?.toIntOrNull()?.let { WheelItem.Id(it) }
                ?.let { itemId -> itemRoller.allItemsFactories.firstOrNull { it.itemId == itemId } }
                ?.create()
                ?: itemRoller.pick()
            return ItemReceive(participant, item)
        }
    }

    @ActionHandler(key = Key.ItemReceive)
    class Handler : Action.Handler<ItemReceive> {

        override suspend fun handle(action: ItemReceive, currentState: GlobalState): GlobalState {
            if (currentState.effectsOf(action.receivedBy).any { it is NoClownery }){
                throw StateModificationException(action, "Can't roll items while `NoClownery` is applied")
            }
            return when (action.item) {
                is NimbleFingers -> when (action.item.canUse(action.receivedBy, currentState)) {
                    true -> currentState.updatePlayer(action.receivedBy) {
                        it.copy(pendingEvents = it.pendingEvents + action.item)
                    }
                    else -> currentState
                }
                is Gamer -> {
                    currentState.updatePlayer(action.receivedBy) { state ->
                        val viewer = state.effects.firstOrNull { it is Viewer }
                        if (viewer != null) {
                            state.copy(effects = state.effects - viewer)
                        } else {
                            state.copy(effects = state.effects + action.item.setActive(state.currentActiveGame == null))
                        }
                    }
                }
                is Viewer -> {
                    currentState.updatePlayer(action.receivedBy) { state ->
                        val gamer = state.effects.firstOrNull { it is Gamer }
                        if (gamer != null) {
                            state.copy(effects = state.effects - gamer)
                        } else {
                            state.copy(effects = state.effects + action.item.setActive(state.currentActiveGame == null))
                        }
                    }
                }
                is BabySupport -> {
                    currentState.updatePlayer(action.receivedBy) { state ->
                        if (currentState.stateSnapshot.playersState.minOf { it.value.position } == state.position) {
                            state.copy(effects = state.effects + action.item)
                        } else {
                            state
                        }
                    }
                }
                is WheelItem.InventoryItem -> currentState.updatePlayer(action.receivedBy) {
                    it.copy(inventory = it.inventory + action.item)
                }
                is WheelItem.Effect -> currentState.updatePlayer(action.receivedBy) {
                    it.copy(effects = it.effects + action.item)
                }
                is WheelItem.Event -> action.item.invoke(currentState, action.receivedBy)
                is WheelItem.PendingEvent -> currentState.updatePlayer(action.receivedBy) {
                    it.copy(pendingEvents = it.pendingEvents + action.item)
                }
            }
        }
    }
}
