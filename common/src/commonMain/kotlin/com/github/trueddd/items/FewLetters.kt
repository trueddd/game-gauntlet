package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.FewLetters}")
class FewLetters private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        const val SYMBOLS_LIMIT = 10
        fun create() = FewLetters(uid = generateWheelItemUid())
    }

    override val id = Id(FewLetters)

    override val name = "Мало букв"

    override val description = """
        На следующем ролле следует проходить ту игру в названии которой меньше десяти букв и цифр.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(FewLetters)
        override fun create() = Companion.create()
    }
}
