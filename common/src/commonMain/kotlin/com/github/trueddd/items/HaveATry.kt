package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.HaveATry}")
class HaveATry private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = HaveATry(uid = generateWheelItemUid())
    }

    override val id = Id(HaveATry)

    override val name = "\"Ну, попробуй\""

    override val description = """
        |При использовании можете воспользоваться гайдом, видеопрохождением или спидраном игры. Имеет 1 заряд.
    """.removeTabs()

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(inventory = playerState.inventory.without(uid))
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(HaveATry)
        override fun create() = Companion.create()
    }
}
