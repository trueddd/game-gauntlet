package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class LoyalModerator private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = LoyalModerator(uid = generateWheelItemUid())
    }

    override val id = Id.LoyalModerator

    override val name = "Верный модер"

    override val description = """
        Принимает выбранный стримером дебафф на себя и уничтожает его. 
        Не имеет временных ограничений и может быть использовано в любой момент.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            val targetDebuffId = arguments.firstOrNull()
                ?: throw IllegalArgumentException("Debuff uid must be specified")
            if (playerState.effects.firstOrNull { it.uid == targetDebuffId } is EasterCakeBang) {
                throw IllegalArgumentException("This debuff cannot be dispelled")
            }
            playerState.copy(
                effects = playerState.effects.without(targetDebuffId),
                inventory = playerState.inventory.without(uid),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.LoyalModerator
        override fun create() = LoyalModerator.create()
    }
}
