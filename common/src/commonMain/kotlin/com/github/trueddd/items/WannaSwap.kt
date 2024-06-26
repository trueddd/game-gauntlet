package com.github.trueddd.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.WannaSwap}")
class WannaSwap private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<PlayerName>> {

    companion object {
        fun create() = WannaSwap(uid = generateWheelItemUid())
    }

    override val id = Id(WannaSwap)

    override val name = "\"Махнёмся не глядя?\""

    override val description = """
        |Выбери стримера и предожи поменятся с ним играми. Если другой стример не согласен на обмен, 
        |то обоими стримерами бросается кубик d6 без модификаторов, если выигрывает стример, предложивший обмен, 
        |то стримеры меняются играми, иначе сделка срывается. При удачной смене прохождение игр у обоих стримеров 
        |начинается с начала.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Player(
            name = "Другой игрок",
            description = "Укажи себя, если обмен не требуется"
        ))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<PlayerName> {
        return Parameters.One(rawArguments.getParticipantParameter(index = 0, currentState)!!)
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val swapPlayer = getParameters(arguments, globalState).parameter1
        if (swapPlayer == usedBy) {
            return globalState.updatePlayers { playerName, playerState ->
                when (playerName) {
                    usedBy -> playerState.copy(pendingEvents = playerState.pendingEvents.without(uid))
                    else -> playerState
                }
            }
        }
        val userGame = globalState.stateOf(usedBy).currentActiveGame
            ?: throw IllegalStateException("The game of user must be in active state")
        val gameToSwap = globalState.stateOf(swapPlayer).currentActiveGame
            ?: throw IllegalStateException("The game of another player must be in active state")
        return globalState.updateGameHistory(usedBy) { history ->
            history.dropLast(1) + gameToSwap.copy(status = Game.Status.InProgress)
        }.updateGameHistory(swapPlayer) { history ->
            history.dropLast(1) + userGame.copy(status = Game.Status.InProgress)
        }
            .updateCurrentGame(usedBy)
            .updateCurrentGame(swapPlayer)
            .updatePlayer(usedBy) { it.copy(pendingEvents = it.pendingEvents.without(uid)) }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(WannaSwap)
        override fun create() = Companion.create()
    }
}
