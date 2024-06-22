package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.YouDoNotNeedThis}")
class YouDoNotNeedThis private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = YouDoNotNeedThis(uid = generateWheelItemUid())
    }

    override val id = Id(YouDoNotNeedThis)

    override val name = "Тебе это и не нужно"

    override val description = "Сбрасывает случайный бафф."

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        return globalState.updatePlayer(triggeredBy) { state ->
            val buff = state.effects
                .filterIsInstance<Effect.Buff>()
                .randomOrNull()
                ?: return@updatePlayer state
            state.copy(effects = state.effects - buff)
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(YouDoNotNeedThis)
        override fun create() = Companion.create()
    }
}
