package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.MinusToEveryone}")
class MinusToEveryone private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = MinusToEveryone(uid = generateWheelItemUid())
    }

    override val id = Id(MinusToEveryone)

    override val name = "Минус всем"

    override val description = "`-1` на следующий бросок кубика для всех участников ивента."

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayers { _, state ->
            state.copy(effects = state.effects + com.github.trueddd.items.MinusOneDebuff.create())
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(MinusToEveryone)
        override fun create() = Companion.create()
    }
}
