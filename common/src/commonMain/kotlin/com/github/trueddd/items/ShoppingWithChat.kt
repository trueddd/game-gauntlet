package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.ShoppingWithChat}")
class ShoppingWithChat private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = ShoppingWithChat(uid = generateWheelItemUid())
    }

    override val id = Id(ShoppingWithChat)

    override val name = "По магазинам с чатом"

    override val description = """
        Произведите реролл колеса. Чат выбирает между выпавшим пунктом и четырьмя соседними путем голосования (/poll). 
        Во время голосования запрещено использовать любые манипуляции с чатом 
        (фолловер или саб моды, приоритет голосования у сабов, голосование за поинты и т.д.).
    """.trimIndent()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(ShoppingWithChat)
        override fun create() = Companion.create()
    }
}
