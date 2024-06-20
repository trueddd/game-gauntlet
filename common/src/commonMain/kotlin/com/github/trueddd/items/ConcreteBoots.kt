package com.github.trueddd.items

import com.github.trueddd.utils.Log
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class ConcreteBoots private constructor(
    override val uid: String,
    override val chargesLeft: Int,
) : WheelItem.Effect.Debuff(), WithCharges<ConcreteBoots> {

    companion object {

        fun create(chargesLeft: Int = MOVES_COUNT) = ConcreteBoots(
            uid = generateWheelItemUid(),
            chargesLeft = chargesLeft
        )

        const val MOVE_DIVISOR = 2
        const val MOVES_COUNT = 3
    }

    override val id = Id(ConcreteBoots)

    override val name = "Бетонная обувь"

    override val description = """
        |Уменьшает количество проходимых секторов при броске на ход в $MOVE_DIVISOR раза. 
        |Эффект применяется после подсчёта всех модификаторов и держится $MOVES_COUNT хода. 
        |Предотвращает перемещения, вызванные "Ядерной бомбой", "Торнадо", "Землятрясением".
    """.removeTabs()

    override val maxCharges: Int
        get() = MOVES_COUNT

    override fun useCharge(): WithCharges<ConcreteBoots> {
        Log.info("ConcreteBoots", "useCharge($uid, ${chargesLeft - 1})")
        return ConcreteBoots(uid = uid, chargesLeft = chargesLeft - 1)
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(ConcreteBoots)
        override fun create() = Companion.create(MOVES_COUNT)
    }
}
