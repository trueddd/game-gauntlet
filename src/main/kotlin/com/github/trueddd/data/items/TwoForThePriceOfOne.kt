package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class TwoForThePriceOfOne private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = TwoForThePriceOfOne(uid = generateWheelItemUid())
    }

    override val id = Id.TwoForThePriceOfOne

    override val name = "Два по цене одного"

    override val description = """
        Произведите реролл колеса. Выполните два соседних пункта от выпавшего. Порядок: сначала верх, потом низ.
    """.trimIndent()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.TwoForThePriceOfOne
        override fun create() = TwoForThePriceOfOne.create()
    }
}
