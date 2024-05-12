package com.github.trueddd.actions

import com.github.trueddd.data.GlobalState
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a${Action.Key.GlobalEvent}")
data class GlobalEvent(
    @SerialName("ty")
    val type: Type,
    @SerialName("sn")
    val stageNumber: Int,
) : Action(Key.GlobalEvent) {

    enum class Type {
        Tornado, Nuke
    }

    @ActionHandler(key = Key.GlobalEvent)
    class Handler : Action.Handler<GlobalEvent> {

        override suspend fun handle(action: GlobalEvent, currentState: GlobalState): GlobalState {
            return when (action.type) {
                Type.Tornado -> TODO()
                Type.Nuke -> TODO()
            }
        }
    }
}
