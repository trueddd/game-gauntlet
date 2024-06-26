package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.HoleyPockets}")
class HoleyPockets private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = HoleyPockets(uid = generateWheelItemUid())
    }

    override val id = Id(HoleyPockets)

    override val name = "Дырявые карманы"

    override val description = "Сбрасывает все дебаффы, баффы и предметы."

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        return globalState.updatePlayer(triggeredBy) { playerState ->
            playerState.copy(
                inventory = emptyList(),
                effects = playerState.effects.filter { it is NonDroppable },
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(HoleyPockets)
        override fun create() = Companion.create()
    }
}
