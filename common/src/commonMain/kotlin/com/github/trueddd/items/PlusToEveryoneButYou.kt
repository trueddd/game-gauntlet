package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.PlusToEveryoneButYou}")
class PlusToEveryoneButYou private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = PlusToEveryoneButYou(uid = generateWheelItemUid())
    }

    override val id = Id(PlusToEveryoneButYou)

    override val name = "Плюс всем, минус тебе"

    override val description = """
        |`+1` на следующий бросок кубика для всех участников ивента, кроме участника, нароллившего этот пункт. 
        |Наролливший этот пункт участник получает `-1` к кубику к своему следующему броску.
    """.removeTabs()

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        return globalState.updatePlayers { playerName, state ->
            if (playerName == triggeredBy) {
                state.copy(effects = state.effects + com.github.trueddd.items.MinusOneDebuff.create())
            } else {
                state.copy(effects = state.effects + com.github.trueddd.items.PlusOneBuff.create())
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(PlusToEveryoneButYou)
        override fun create() = Companion.create()
    }
}
