package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.ClimbingRope}")
class ClimbingRope private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = ClimbingRope(uid = generateWheelItemUid())
    }

    override val id = Id(ClimbingRope)

    override val name = "Альпинистский трос"

    override val description = "При дропе позволяет откатиться на 1 сектор назад."

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                effects = playerState.effects + Buff.create(),
                inventory = playerState.inventory.without(uid),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(ClimbingRope)
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(override val uid: String) : Effect.Buff() {

        companion object {
            fun create() = Buff(uid = generateWheelItemUid())
        }

        override val id = Id(ClimbingRope)

        override val name = "Альпинистский трос"

        override val description = "При дропе позволяет откатиться на 1 сектор назад. Бафф пропадает после дропа или смены игры."
    }
}
