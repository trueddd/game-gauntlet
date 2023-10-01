package com.github.trueddd.items

import kotlinx.serialization.Serializable

@Serializable
class MinusOneDebuff private constructor(override val uid: String) : WheelItem.Effect.Debuff(), DiceRollModifier {

    companion object {
        fun create() = MinusOneDebuff(uid = generateWheelItemUid())
    }

    override val id = Id.MinusOneDebuff

    override val name = "Минус один"

    override val description = "-1 к следующему броску кубика на ход"

    override val modifier = -1
}
