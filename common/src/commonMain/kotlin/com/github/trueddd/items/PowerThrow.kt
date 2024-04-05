package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.PowerThrow}")
class PowerThrow private constructor(
    override val uid: String,
    override val chargesLeft: Int
) : WheelItem.Effect.Buff(), DiceRollModifier, WithCharges<PowerThrow> {

    companion object {
        fun create() = PowerThrow(uid = generateWheelItemUid(), chargesLeft = 1)
        fun create(chargesLeft: Int) = PowerThrow(uid = generateWheelItemUid(), chargesLeft)
    }

    override val id = Id(PowerThrow)

    override val name = "Мощный бросок"

    override val description = """
        К следующему броску кубика для перехода по секторам прибавьте 1. 
        Общее значение не может быть больше 10. 
        В таком случае излишек переносится на следующий бросок кубика.
    """.trimIndent()

    override val modifier: Int = 1

    override val maxCharges: Int = 1

    override fun useCharge(): WithCharges<PowerThrow> {
        return PowerThrow(uid, chargesLeft = chargesLeft - 1)
    }

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(PowerThrow)
        override fun create() = Companion.create()
    }
}
