package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

// TODO: add warning on item wheel front-end when this effect is applied
@Serializable
class NoClownery private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        fun create() = NoClownery(uid = generateWheelItemUid())
    }

    override val id = Id.NoClownery

    override val name = "Никакой клоунады"

    override val description = """
        До пересечения следующего спец-сектора запрещается крутить колесо приколов. Сбросить этот дебафф невозможно.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override fun create() = NoClownery.create()
    }
}
