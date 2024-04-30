package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.BabySupport}")
class BabySupport private constructor(
    override val uid: String,
    override val chargesLeft: Int
) : WheelItem.Effect.Buff(), WithCharges<BabySupport>, DiceRollModifier {

    companion object {
        fun create() = BabySupport(uid = generateWheelItemUid(), chargesLeft = 2)
    }

    override val id = Id(BabySupport)

    override val name = "Поддержка малютки"

    override val description = """
        |Если стример, выбивший этот бафф, является аутсайдером (последний или один из последних на карте), 
        |то он получает `+2` к результату броска кубика на следующие 2 хода. В ином случае реролл.
    """.removeTabs()

    override val modifier = 2

    override val maxCharges = 2

    override fun useCharge(): WithCharges<BabySupport> {
        return BabySupport(uid = uid, chargesLeft = chargesLeft - 1)
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(BabySupport)
        override fun create() = Companion.create()
    }
}
