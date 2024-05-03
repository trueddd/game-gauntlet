package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.NoClownery}")
class NoClownery private constructor(override val uid: String) : WheelItem.Effect.Debuff(), NonDroppable {

    companion object {
        fun create() = NoClownery(uid = generateWheelItemUid())
    }

    override val id = Id(NoClownery)

    override val name = "Никакой клоунады"

    override val description = """
        |До пересечения следующего спец-сектора запрещается крутить колесо приколов. Сбросить этот дебафф невозможно.
    """.removeTabs()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(NoClownery)
        override fun create() = Companion.create()
    }
}
