package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class Radio private constructor(
    override val uid: String,
    override val chargesLeft: Int
) : WheelItem.Effect.Debuff(), WithCharges<Radio> {

    companion object {
        fun create() = Radio(uid = generateWheelItemUid(), chargesLeft = 2)
    }

    override val id = Id(Radio)

    override val name = "Радио"

    override val description = """
        |Поздравляем, теперь у вас есть радиоприемник! Текущая и следующая игра проходится под радио. 
        |Если сейчас нет игры, то дебафф действует следующие 2 игры. Эффект пропадает только после прохождения игр.
    """.removeTabs()

    override val maxCharges: Int = 2

    override fun useCharge(): WithCharges<Radio> {
        return Radio(uid = uid, chargesLeft = chargesLeft - 1)
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Radio)
        override fun create() = Companion.create()
    }
}
