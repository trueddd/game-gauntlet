package com.github.trueddd.data.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class WannaSwap private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = WannaSwap(uid = generateWheelItemUid())
    }

    override val id = Id.WannaSwap

    override val name = "\"Махнёмся не глядя?\""

    override val description = """
        Выбери стримера и предожи поменятся с ним играми. Если другой стример не согласен на обмен, 
        то обоими стримерами бросается кубик d6 без модификаторов, если выигрывает стример, предложивший обмен, 
        то стримеры меняются играми, иначе сделка срывается. При удачной смене прохождение игр у обоих стримеров 
        начинается с начала.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val swapPlayer = arguments.getParticipantParameter(index = 0, globalState)
        val userGame = globalState[usedBy.name]!!.currentActiveGame
            ?: throw IllegalStateException("The game of user must be in active state")
        val gameToSwap = globalState[swapPlayer.name]!!.currentActiveGame
            ?: throw IllegalStateException("The game of another player must be in active state")
        return globalState.updatePlayers { participant, playerState ->
            when (participant.name) {
                usedBy.name -> playerState.copy(
                    gameHistory = playerState.updatedHistoryWithLast { gameToSwap.copy(status = Game.Status.InProgress) },
                    pendingEvents = playerState.pendingEvents.without(uid),
                )
                swapPlayer.name -> playerState.copy(
                    gameHistory = playerState.updatedHistoryWithLast { userGame.copy(status = Game.Status.InProgress) },
                )
                else -> playerState
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.WannaSwap
        override fun create() = WannaSwap.create()
    }
}
