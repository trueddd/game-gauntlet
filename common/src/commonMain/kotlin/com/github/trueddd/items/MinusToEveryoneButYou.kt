package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class MinusToEveryoneButYou private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = MinusToEveryoneButYou(uid = generateWheelItemUid())
    }

    override val id = Id.MinusToEveryoneButYou

    override val name = "Минус всем, плюс тебе"

    override val description = """
        -1 на следующий бросок кубика для всех участников ивента, кроме участника, нароллившего этот пункт. 
        Наролливший этот пункт участник получает +1 к кубику к своему следующему броску.
    """.trimIndent()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayers { player, state ->
            if (player.name == rolledBy.name) {
                state.copy(effects = state.effects + PlusOneBuff.create())
            } else {
                state.copy(effects = state.effects + MinusOneDebuff.create())
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.MinusToEveryoneButYou
        override fun create() = Companion.create()
    }
}
