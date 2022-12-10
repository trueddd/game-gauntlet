package com.github.trueddd.data.items

import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class WeakThrow(
    override val modifier: Int = -1,
) : InventoryItem.Effect.Debuff(), DiceRollModifier {

    override val id = Id.WeakThrow

    override val name = "Слабый бросок"

    override fun toString(): String {
        return "${super.toString()}[mod=$modifier]"
    }

    @IntoSet(setName = Factory.SET_NAME)
    class WeakThrowFactory : Factory() {
        override fun create() = WeakThrow()
    }
}
