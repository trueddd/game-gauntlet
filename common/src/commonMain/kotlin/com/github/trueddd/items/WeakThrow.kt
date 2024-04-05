package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.WeakThrow}")
class WeakThrow private constructor(
    override val uid: String,
    override val chargesLeft: Int
) : WheelItem.Effect.Debuff(), DiceRollModifier, WithCharges<WeakThrow> {

    companion object {
        fun create() = WeakThrow(uid = generateWheelItemUid(), chargesLeft = 1)
    }

    override val id = Id(WeakThrow)

    override val name = "Слабый бросок"

    override val description = """
        От следующего броска кубика для перехода по секторам отнимется 1. 
        Общее значение не может равно 0. 
        В таком случае отрицательные эффекты на бросок переносятся на следующий бросок кубика.
    """.trimIndent()

    override val modifier: Int = -1

    override val maxCharges: Int = 1

    override fun useCharge(): WithCharges<WeakThrow> {
        return WeakThrow(uid, chargesLeft = chargesLeft - 1)
    }

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(WeakThrow)
        override fun create() = Companion.create()
    }
}
