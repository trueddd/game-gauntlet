package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.PlusToEveryone}")
class PlusToEveryone private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = PlusToEveryone(uid = generateWheelItemUid())
    }

    override val id = Id(PlusToEveryone)

    override val name = "Плюс всем"

    override val description = "`+1` на следующий бросок кубика для всех участников ивента."

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        return globalState.updatePlayers { _, state ->
            state.copy(effects = state.effects + com.github.trueddd.items.PlusOneBuff.create())
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(PlusToEveryone)
        override fun create() = Companion.create()
    }
}
