package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class WeakThrow private constructor(
    override val uid: String,
    override val chargesLeft: Int
) : WheelItem.Effect.Debuff(), DiceRollModifier, WithCharges {

    companion object {
        fun create() = WeakThrow(uid = generateWheelItemUid(), chargesLeft = 1)
    }

    override val id = Id.WeakThrow

    override val name = "Слабый бросок"

    override val modifier: Int = -1

    override val maxCharges: Int = 1

    override fun useCharge(): WithCharges {
        return WeakThrow(uid, chargesLeft = chargesLeft - 1)
    }

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @IntoSet(setName = WheelItem.Factory.SetTag)
    class Factory : WheelItem.Factory {
        override fun create() = WeakThrow.create()
    }
}
