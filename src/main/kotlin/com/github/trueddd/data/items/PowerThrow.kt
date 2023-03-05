package com.github.trueddd.data.items

import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class PowerThrow private constructor(override val uid: Long) : WheelItem.Effect.Buff(), DiceRollModifier {

    companion object {
        fun create() = PowerThrow(uid = System.currentTimeMillis())
    }

    override val id = Id.PowerThrow

    override val name = "Мощный бросок"

    override val modifier: Int = 1

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @IntoSet(setName = Factory.SET_NAME)
    class PowerThrowFactory : Factory() {
        override fun create() = PowerThrow.create()
    }
}
