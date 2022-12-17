package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class SamuraiLunge(
    override val uid: Long,
    override val chargesAmount: Int,
) : InventoryItem.Item() {

    override val id = Id.SamuraiLunge

    override val name = "Самурайский выпад"

    override val maxChargesAmount = 1

    override fun toString(): String {
        return "${super.toString()}[$chargesAmount]"
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState): GlobalState {
        return globalState.copy(
            players = globalState.players.mapValues { (user, state) ->
                if (user == usedBy) {
                    state.copy(
                        dropPenaltyReversed = true,
                        inventory = state.inventory.filter { it.uid != uid },
                    )
                } else {
                    state
                }
            }
        )
    }

    @IntoSet(setName = InventoryItem.Factory.SET_NAME)
    class Factory : InventoryItem.Factory() {
        override fun create() = SamuraiLunge(uid = System.currentTimeMillis(), chargesAmount = 1)
    }
}
