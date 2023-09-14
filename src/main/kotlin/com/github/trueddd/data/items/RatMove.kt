package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class RatMove private constructor(override val uid: String) : WheelItem.Event(), Usable {

    companion object {
        fun create() = RatMove(uid = generateWheelItemUid())
    }

    override val id = Id.RatMove

    override val name = "Крысиный поступок"

    override val description = """
        Выбери стримера и сбрось его инвентарь, баффы и дебаффы. Теперь ты главная крыса ивента. 
        Нельзя сбросить пустой инвентарь, нельзя сбросить свой инвентарь, если у всех стримеров пустой инвентарь, 
        то пункт рероллится.
    """.trimIndent()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayer(rolledBy) { playerState ->
            playerState.copy(pendingEvents = playerState.pendingEvents + this)
        }
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val targetUser = arguments.first()
        return globalState.updatePlayers { participant, playerState ->
            when (participant.name) {
                usedBy.name -> playerState.copy(pendingEvents = playerState.pendingEvents.filter { it.uid != uid })
                targetUser -> playerState.copy(
                    inventory = emptyList(),
                    effects = emptyList(),
                )
                else -> playerState
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.RatMove
        override fun create() = RatMove.create()
    }
}
