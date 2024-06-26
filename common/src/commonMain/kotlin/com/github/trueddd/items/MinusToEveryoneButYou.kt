package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.MinusToEveryoneButYou}")
class MinusToEveryoneButYou private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = MinusToEveryoneButYou(uid = generateWheelItemUid())
    }

    override val id = Id(MinusToEveryoneButYou)

    override val name = "Минус всем, плюс тебе"

    override val description = """
        |`-1` на следующий бросок кубика для всех участников ивента, кроме участника, нароллившего этот пункт. 
        |Наролливший этот пункт участник получает `+1` к кубику к своему следующему броску.
    """.removeTabs()

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        return globalState.updatePlayers { playerName, state ->
            if (playerName == triggeredBy) {
                state.copy(effects = state.effects + com.github.trueddd.items.PlusOneBuff.create())
            } else {
                state.copy(effects = state.effects + com.github.trueddd.items.MinusOneDebuff.create())
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(MinusToEveryoneButYou)
        override fun create() = Companion.create()
    }
}
