package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.TestOnly

@Serializable
class PowerThrow private constructor(
    override val uid: String,
    override val chargesLeft: Int
) : WheelItem.Effect.Buff(), DiceRollModifier, WithCharges {

    companion object {
        fun create() = PowerThrow(uid = generateWheelItemUid(), chargesLeft = 1)
        @TestOnly
        fun create(chargesLeft: Int) = PowerThrow(uid = generateWheelItemUid(), chargesLeft)
    }

    override val id = Id.PowerThrow

    override val name = "Мощный бросок"

    override val modifier: Int = 1

    override val maxCharges: Int = 1

    override fun useCharge(): WithCharges {
        return PowerThrow(uid, chargesLeft = chargesLeft - 1)
    }

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @IntoSet(setName = WheelItem.Factory.SetTag)
    class Factory : WheelItem.Factory {
        override fun create() = PowerThrow.create()
    }
}
