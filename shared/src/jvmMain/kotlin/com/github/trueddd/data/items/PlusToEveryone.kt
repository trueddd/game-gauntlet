package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class PlusToEveryone private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = PlusToEveryone(uid = generateWheelItemUid())
    }

    override val id = Id.PlusToEveryone

    override val name = "Плюс всем"

    override val description = "+1 на следующий бросок кубика для всех участников ивента."

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayers { _, state ->
            state.copy(effects = state.effects + PlusOneBuff.create())
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.PlusToEveryone
        override fun create() = PlusToEveryone.create()
    }
}
