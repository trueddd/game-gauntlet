package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import kotlinx.serialization.Serializable

@Serializable
class LostLeg private constructor(override val uid: String) : WheelItem.Effect.Debuff(), DiceRollModifier {

    companion object {
        fun create() = LostLeg(uid = generateWheelItemUid())
    }

    override val id = Id(LostLeg)

    override val name = "Отрыв ноги"

    override val description = """
        |Вы были рядом с эпицентром и потеряли ногу - `-3` к броску следующего кубика.
    """.removeTabs()

    override val modifier: Int
        get() = -3
}
