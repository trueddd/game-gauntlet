package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Viewer}")
class Viewer private constructor(
    override val uid: String,
    override val chargesLeft: Int,
    override val isActive: Boolean
) : WheelItem.Effect.Debuff(), WithCharges<Viewer>, Activatable<Viewer> {

    companion object {
        fun create() = Viewer(uid = generateWheelItemUid(), chargesLeft = 2, isActive = true)
    }

    override val id = Id(Viewer)

    override val name = "Видосник"

    override val description = """
        |Следующие две игры, не считая текущей, проходятся на самом сложном уровне сложности. 
        |При наличии "Игровика" оба эффекта уничтожают друг друга.
    """.removeTabs()

    override val maxCharges = 2

    override fun useCharge(): WithCharges<Viewer> {
        return Viewer(uid, chargesLeft = if (!isActive) maxCharges else chargesLeft - 1, isActive)
    }

    override fun setActive(value: Boolean): Viewer {
        return Viewer(uid, chargesLeft, isActive = value)
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Viewer)
        override fun create() = Companion.create()
    }
}
