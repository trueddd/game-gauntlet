package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class SamuraiLunge private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = SamuraiLunge(uid = generateWheelItemUid())
    }

    override val id = Id.SamuraiLunge

    override val name = "Самурайский выпад"

    override val description = """
        При выпадении этого пункта позволяет не откатываться назад при дропе игры, 
        а походить вперед. Имеет 1 заряд.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        Log.info(name, "Using item by ${usedBy.name}")
        return globalState.updatePlayer(usedBy) { state ->
            state.copy(
                effects = state.effects + DropReverse.create(),
                inventory = state.inventory.filter { it.uid != uid },
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override fun create() = SamuraiLunge.create()
    }
}
