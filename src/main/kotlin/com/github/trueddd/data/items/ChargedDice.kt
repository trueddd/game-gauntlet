package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class ChargedDice private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        fun create() = ChargedDice(uid = generateWheelItemUid())
    }

    override val id = Id.ChargedDice

    override val name = "Заряженный кубик"

    override val description = """
        Следующий бросок кубика на ход (бросок кубика на дроп не считается) откатывает стримера назад 
        в зависимости от выпавшего значения, учитывая все модификаторы.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override fun create() = ChargedDice.create()
    }
}
