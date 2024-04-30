package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.SamuraiLunge}")
class SamuraiLunge private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = SamuraiLunge(uid = generateWheelItemUid())
    }

    override val id = Id(SamuraiLunge)

    override val name = "Самурайский выпад"

    override val description = """
        |При выпадении этого пункта позволяет не откатываться назад при дропе игры, 
        |а походить вперед. Для этого предмет необходимо использовать.
    """.removeTabs()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { state ->
            state.copy(
                effects = state.effects + Buff.create(),
                inventory = state.inventory.without(uid),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(SamuraiLunge)
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(override val uid: String) : Effect.Buff() {

        companion object {
            fun create() = Buff(uid = generateWheelItemUid())
        }

        override val id = Id(SamuraiLunge)

        override val name = "Самурайский выпад"

        override val description = "При дропе игры позволяет не откатываться назад, а походить вперед."
    }
}
