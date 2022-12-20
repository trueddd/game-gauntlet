package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.Log
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class SamuraiLunge private constructor(
    override val uid: Long,
    override val chargesAmount: Int,
) : InventoryItem.Item() {

    companion object {
        fun create() = SamuraiLunge(uid = System.currentTimeMillis(), chargesAmount = 1)
    }

    override val id = Id.SamuraiLunge

    override val name = "Самурайский выпад"

    override val maxChargesAmount = 1

    override fun toString(): String {
        return "${super.toString()}[$chargesAmount]"
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState): GlobalState {
        Log.info(name, "Using item by ${usedBy.name}")
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
        override fun create() = SamuraiLunge.create()
    }
}
