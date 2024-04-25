package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Reroll}")
class Reroll private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = Reroll(uid = generateWheelItemUid())
    }

    override val id = Id(Reroll)

    override val name = "Ты думал здесь что-то будет?"

    override val description = "Произведите реролл колеса приколов."

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Reroll)
        override fun create() = Companion.create()
    }
}
