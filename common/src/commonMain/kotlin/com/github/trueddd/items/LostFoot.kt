package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import kotlinx.serialization.Serializable

@Serializable
class LostFoot private constructor(override val uid: String) : WheelItem.Effect.Debuff(), DiceRollModifier {

    companion object {
        fun create() = LostFoot(uid = generateWheelItemUid())
    }

    override val id = Id(LostFoot)

    override val name = "Отрыв стопы"

    override val description = """
        |Вы угодили в зону поражения недавнего бедствия - `-1` к броску следующего кубика. 
        |Ничего, скоро заживёт, всё могло пойти гораздо хуже.
    """.removeTabs()

    override val modifier: Int
        get() = -1
}
