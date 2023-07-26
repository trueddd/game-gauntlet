package com.github.trueddd.data.items

import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class WeakThrow private constructor(override val uid: Long) : WheelItem.Effect.Debuff(), DiceRollModifier {

    companion object {
        fun create() = WeakThrow(uid = System.currentTimeMillis())
    }

    override val id = Id.WeakThrow

    override val name = "Слабый бросок"

    override val modifier: Int = -1

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @IntoSet(setName = WheelItem.Factory.SetTag)
    class Factory : WheelItem.Factory {
        override fun create() = WeakThrow.create()
    }
}
