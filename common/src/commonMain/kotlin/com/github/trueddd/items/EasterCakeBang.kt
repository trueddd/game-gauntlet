package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class EasterCakeBang private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        fun create() = EasterCakeBang(uid = generateWheelItemUid())
    }

    override val id = Id.EasterCakeBang

    override val name = "Куличёвский тарабан"

    override val description = """
        Запрещает дропать текущую игру! Текущая игра должна быть пройдена до конца, 
        иначе стримера будет тарабанить в очело весь его чат на протяжении недели. 
        Пункт мощнее других пунктов и его нельзя отменить. Настоящая мотивация для прохождения для омежек стримеров, 
        которые любят мошнить и дропать все подряд по абсолютно любому поводу, 
        но теперь их грязный анус будет в опастности и они тысячу раз подумают, стоит ли идти на столь гнусный шаг.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.EasterCakeBang
        override fun create() = Companion.create()
    }
}
