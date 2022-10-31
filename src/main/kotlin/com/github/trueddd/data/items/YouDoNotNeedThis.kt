package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

@Serializable
class YouDoNotNeedThis : InventoryItem.Event(), InTimeEvent {

    override val id = Id.YouDoNotNeedThis
    override val name = "Тебе это и не нужно"

    override fun toString(): String {
        return super.toString()
    }

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
}
