package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Plasticine}")
class Plasticine private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = Plasticine(uid = generateWheelItemUid())
    }

    override val id = Id(Plasticine)

    override val name = "Пластилин"

    override val description = """
        Позволяет превратить этот предмет в любой другой предмет по выбору стримера.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState
    }

    fun transform(
        user: Participant,
        globalState: GlobalState,
        arguments: List<String>,
        factories: Set<WheelItem.Factory>
    ): GlobalState {
        val itemId = arguments.getIntParameter()
        val factory = factories.first { it.itemId.value == itemId }
        val item = factory.create() as InventoryItem
        return globalState.updatePlayer(user) { playerState ->
            playerState.copy(
                inventory = playerState.inventory.without(uid) + item,
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Plasticine)
        override fun create() = Companion.create()
    }
}
