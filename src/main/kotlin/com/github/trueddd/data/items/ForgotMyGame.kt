package com.github.trueddd.data.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class ForgotMyGame private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = ForgotMyGame(uid = generateWheelItemUid())
    }

    override val id = Id.ForgotMyGame

    override val name = "\"Я забыл, во что играл...\""

    override val description = "При выпадении этого пункта стример может рероллнуть игру."

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val shouldReroll = arguments.firstOrNull().let {
            when (it) {
                "1" -> true
                "0" -> false
                else -> throw IllegalArgumentException(
                    "Result should be sent as `0` or `1` (for negative and positive responses), but was $it"
                )
            }
        }
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                gameHistory = if (shouldReroll) {
                    playerState.updatedHistoryWithLast { it.copy(status = Game.Status.Rerolled) }
                } else {
                    playerState.gameHistory
                },
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.ForgotMyGame
        override fun create() = ForgotMyGame.create()
    }
}
