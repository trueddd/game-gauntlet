package com.github.trueddd.core.actions

import com.github.trueddd.core.ItemRoller
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.WheelItem
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class ItemReceive(
    val receivedBy: Participant,
    val item: WheelItem,
) : Action(Keys.ItemReceive) {

    @IntoSet(Action.Generator.SetTag)
    class Generator(private val itemRoller: ItemRoller) : Action.Generator<ItemReceive> {

        override val inputMatcher by lazy {
            Regex("${Commands.ItemReceive} ${Action.Generator.ParticipantGroup}", RegexOption.DOT_MATCHES_ALL)
        }

        override fun generate(matchResult: MatchResult): ItemReceive {
            val actor = matchResult.groupValues.lastOrNull()!!
            val item = itemRoller.pick()
            return ItemReceive(Participant(actor), item)
        }
    }

    @IntoMap(mapName = Action.Handler.MapTag, key = Keys.ItemReceive)
    class Handler : Action.Handler<ItemReceive> {

        override suspend fun handle(action: ItemReceive, currentState: GlobalState): GlobalState {
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
}
