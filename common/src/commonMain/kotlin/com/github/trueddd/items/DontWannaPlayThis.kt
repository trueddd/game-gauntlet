package com.github.trueddd.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.DontWannaPlayThis}")
class DontWannaPlayThis private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = DontWannaPlayThis(uid = generateWheelItemUid())
    }

    override val id = Id(DontWannaPlayThis)

    override val name = "\"Я не хочу играть в это\""

    override val description = """
        Позволяет реролльнуть игру. Имеет 1 заряд.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        if (globalState.stateOf(usedBy).currentGame?.status != Game.Status.InProgress) {
            throw IllegalStateException("Can't reroll game that is not in progress")
        }
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                inventory = playerState.inventory.without(uid),
                currentGame = playerState.currentGame?.copy(status = Game.Status.Rerolled),
            )
        }.updateGameHistory(usedBy) { history ->
            val gameToReroll = history.lastOrNull { it.status == Game.Status.InProgress }
                ?: return@updateGameHistory history
            history.dropLast(1) + gameToReroll
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(DontWannaPlayThis)
        override fun create() = Companion.create()
    }
}
