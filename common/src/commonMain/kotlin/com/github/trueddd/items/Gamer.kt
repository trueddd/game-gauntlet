package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Gamer}")
class Gamer private constructor(
    override val uid: String,
    override val chargesLeft: Int,
    override val isActive: Boolean
) : WheelItem.Effect.Buff(), WithCharges<Gamer>, Activatable<Gamer> {

    companion object {
        fun create() = Gamer(uid = generateWheelItemUid(), chargesLeft = 2, isActive = true)
    }

    override val id = Id(Gamer)

    override val name = "Игровик"

    override val description = """
        |Следующие две игры, не считая текущей, проходятся на самом легком уровне сложности. 
        |При наличии "Видосника" оба эффекта уничтожают друг друга.
    """.removeTabs()

    override val maxCharges = 2

    override fun useCharge(): WithCharges<Gamer> {
        return Gamer(uid, chargesLeft = if (!isActive) maxCharges else chargesLeft - 1, isActive)
    }

    override fun setActive(value: Boolean): Gamer {
        return Gamer(uid, chargesLeft, isActive = value)
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Gamer)
        override fun create() = Companion.create()
    }
}
