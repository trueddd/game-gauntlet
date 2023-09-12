package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import kotlinx.serialization.Serializable

@Serializable
class ClimbingRopeBuff private constructor(override val uid: String) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = ClimbingRopeBuff(uid = generateWheelItemUid())
    }

    override val id = Id.ClimbingRopeBuff

    override val name = "Альпинистский трос"

    override val description = "При дропе позволяет откатиться на 1 сектор назад. Бафф пропадает после дропа или смены игры."
}
