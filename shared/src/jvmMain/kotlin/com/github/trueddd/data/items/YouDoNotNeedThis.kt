package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class YouDoNotNeedThis private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = YouDoNotNeedThis(uid = generateWheelItemUid())
    }

    override val id = Id.YouDoNotNeedThis

    override val name = "Тебе это и не нужно"

    override val description = "Сбрасывает случайный бафф."

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayer(rolledBy) { state ->
            val buff = state.effects
                .filterIsInstance<Effect.Buff>()
                .randomOrNull()
                ?: return@updatePlayer state
            state.copy(effects = state.effects - buff)
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.YouDoNotNeedThis
        override fun create() = YouDoNotNeedThis.create()
    }
}
