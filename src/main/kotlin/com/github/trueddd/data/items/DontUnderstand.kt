package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class DontUnderstand private constructor(override val uid: String) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = DontUnderstand(uid = generateWheelItemUid())
    }

    override val id = Id.DontUnderstand

    override val name = "Хупаму"

    override val description = """
        Перед роллом следующей игры в чате запускается голосование с вариантами из чисел от одного до пяти. 
        После голосования роллится игра, но выбирается не выпавшая в генераторе, а одна из пяти отображаемых - 
        позиция которой соответствует победившему в голосовании числу, где игра №1 - верхняя в списке.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.DontUnderstand
        override fun create() = DontUnderstand.create()
    }
}