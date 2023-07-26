package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
class YouDoNotNeedThis private constructor(override val uid: Long) : WheelItem.Event() {

    companion object {
        fun create() = YouDoNotNeedThis(uid = System.currentTimeMillis())
    }

    override val id = Id.YouDoNotNeedThis

    override val name = "Тебе это и не нужно"

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayer(rolledBy) { state ->
            val buff = state.effects
                .filterIsInstance<Effect.Buff>()
                .randomOrNull()
                ?: return@updatePlayer state
            state.copy(effects = state.effects - buff)
        }
    }

    @IntoSet(setName = WheelItem.Factory.SetTag)
    class Factory : WheelItem.Factory {
        override fun create() = YouDoNotNeedThis.create()
    }
}
