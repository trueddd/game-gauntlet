package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.DontCare}")
class DontCare private constructor(override val uid: String) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = DontCare(uid = generateWheelItemUid())
    }

    override val id = Id(DontCare)

    override val name = "Мне *****"

    override val description = """
        Участник, наролливший этот пункт может выбрать следующую игру из общего списка на свое усмотрение.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(DontCare)
        override fun create() = Companion.create()
    }
}
