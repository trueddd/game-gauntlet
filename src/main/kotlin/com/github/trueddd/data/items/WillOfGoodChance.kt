package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import kotlinx.serialization.Serializable

@Serializable
class WillOfGoodChance private constructor(override val uid: String) : WheelItem.Effect.Buff(), DiceRollModifier {

    companion object {
        fun create() = WillOfGoodChance(uid = generateWheelItemUid())
    }

    override val id = Id.WillOfGoodChance

    override val name = "Воля случая. Бафф"

    override val description = """
        +2 к следующему броску кубика для перехода по секторам.
    """.trimIndent()

    override val modifier: Int
        get() = 2
}
