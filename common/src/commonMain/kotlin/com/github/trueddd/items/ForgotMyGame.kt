package com.github.trueddd.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.ForgotMyGame}")
class ForgotMyGame private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Boolean>> {

    companion object {
        fun create() = ForgotMyGame(uid = generateWheelItemUid())
    }

    override val id = Id(ForgotMyGame)

    override val name = "\"Я забыл, во что играл...\""

    override val description = "При выпадении этого пункта стример может рероллнуть игру."

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Bool(name = "Рероллим?"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Boolean> {
        return Parameters.One(rawArguments.getBooleanParameter()!!)
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val shouldReroll = getParameters(arguments, globalState).parameter1
        return if (shouldReroll) {
            globalState.updatePlayer(usedBy) { playerState ->
                playerState.copy(
                    pendingEvents = playerState.pendingEvents.without(uid),
                    currentGame = playerState.currentGame?.copy(status = Game.Status.Rerolled)
                )
            }.updateGameHistory(usedBy) {
                val lastGame = it.lastOrNull() ?: return@updateGameHistory it
                it.take(it.size - 1) + lastGame.copy(status = Game.Status.Rerolled)
            }
        } else {
            globalState.updatePlayer(usedBy) { playerState ->
                playerState.copy(pendingEvents = playerState.pendingEvents.without(uid))
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(ForgotMyGame)
        override fun create() = Companion.create()
    }
}
