package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class NegativeWheel private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = NegativeWheel(uid = generateWheelItemUid())
    }

    override val id = Id.NegativeWheel

    override val name = "Негативное колесо"

    override val description = """
        Прокрути колесо приколов столько раз, сколько на тебе дебаффов в данный момент.
    """.trimIndent()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.NegativeWheel
        override fun create() = NegativeWheel.create()
    }
}
