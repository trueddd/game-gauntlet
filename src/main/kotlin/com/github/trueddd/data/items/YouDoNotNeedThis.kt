package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class YouDoNotNeedThis(override val uid: Long) : InventoryItem.Event(), InTimeEvent {

    override val id = Id.YouDoNotNeedThis

    override val name = "Тебе это и не нужно"

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        val players = globalState.players.mapValues { (player, state) ->
            if (player == rolledBy) {
                val buff = state.inventory
                    .filterIsInstance<Effect.Buff>()
                    .randomOrNull()
                    ?: return globalState
                state.copy(inventory = state.inventory - buff)
            } else {
                state
            }
        }
        return globalState.copy(players = players)
    }

    @IntoSet(setName = Factory.SET_NAME)
    class YouDoNotNeedThisFactory : Factory() {
        override fun create() = YouDoNotNeedThis(uid = System.currentTimeMillis())
    }
}
