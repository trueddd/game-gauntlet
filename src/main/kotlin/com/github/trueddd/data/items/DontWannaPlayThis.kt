package com.github.trueddd.data.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class DontWannaPlayThis private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = DontWannaPlayThis(uid = generateWheelItemUid())
    }

    override val id = Id.DontWannaPlayThis

    override val name = "\"Я не хочу играть в это\""

    override val description = """
        Выбери стримера и сбрось его инвентарь, баффы и дебаффы. 
        Теперь ты главная крыса ивента. Нельзя сбросить пустой инвентарь, нельзя сбросить свой инвентарь, 
        если у всех стримеров пустой инвентарь, то пункт рероллится.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            val currentGame = playerState.currentActiveGame!!
            playerState.copy(
                inventory = playerState.inventory.filter { it.uid != uid },
                gameHistory = playerState.gameHistory.map {
                    if (it.game == currentGame.game) it.copy(status = Game.Status.Rerolled) else it
                },
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.DontWannaPlayThis
        override fun create() = DontWannaPlayThis.create()
    }
}
