package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class HoleyPockets private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = HoleyPockets(uid = generateWheelItemUid())
    }

    override val id = Id.HoleyPockets

    override val name = "Дырявые карманы"

    override val description = "Сбрасывает все дебаффы, баффы и предметы."

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayer(rolledBy) { playerState ->
            playerState.copy(
                inventory = emptyList(),
                effects = emptyList(),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override fun create() = HoleyPockets.create()
    }
}
