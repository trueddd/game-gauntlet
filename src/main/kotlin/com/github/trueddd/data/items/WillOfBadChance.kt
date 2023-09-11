package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import kotlinx.serialization.Serializable

@Serializable
class WillOfBadChance private constructor(override val uid: String) : WheelItem.Effect.Debuff(), DiceRollModifier {

    companion object {
        fun create() = WillOfBadChance(uid = generateWheelItemUid())
    }

    override val id = Id.WillOfBadChance

    override val name = "Воля случая. Дебафф"

    override val description = """
        -2 к следующему броску кубика для перехода по секторам.
    """.trimIndent()

    override val modifier: Int
        get() = -2
}
