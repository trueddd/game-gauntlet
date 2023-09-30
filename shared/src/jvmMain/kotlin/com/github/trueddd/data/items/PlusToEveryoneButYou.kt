package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class PlusToEveryoneButYou private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = PlusToEveryoneButYou(uid = generateWheelItemUid())
    }

    override val id = Id.PlusToEveryoneButYou

    override val name = "Плюс всем, минус тебе"

    override val description = """
        +1 на следующий бросок кубика для всех участников ивента, кроме участника, нароллившего этот пункт. 
        Наролливший этот пункт участник получает -1 к кубику к своему следующему броску.
    """.trimIndent()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayers { player, state ->
            if (player.name == rolledBy.name) {
                state.copy(effects = state.effects + MinusOneDebuff.create())
            } else {
                state.copy(effects = state.effects + PlusOneBuff.create())
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.PlusToEveryoneButYou
        override fun create() = PlusToEveryoneButYou.create()
    }
}
