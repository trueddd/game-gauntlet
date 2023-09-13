package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class DontCare private constructor(override val uid: String) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = DontCare(uid = generateWheelItemUid())
    }

    override val id = Id.DontCare

    override val name = "Мне *****"

    override val description = """
        Участник, наролливший этот пункт может выбрать следующую игру из общего списка на свое усмотрение.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.DontCare
        override fun create() = DontCare.create()
    }
}
