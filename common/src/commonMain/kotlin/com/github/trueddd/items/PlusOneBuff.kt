package com.github.trueddd.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.PlusOneBuff}")
class PlusOneBuff private constructor(override val uid: String) : WheelItem.Effect.Buff(), DiceRollModifier {

    companion object {
        fun create() = PlusOneBuff(uid = generateWheelItemUid())
    }

    override val id = Id(PlusOneBuff)

    override val name = "Плюс один"

    override val description = "+1 к следующему броску кубика на ход"

    override val modifier = 1
}
