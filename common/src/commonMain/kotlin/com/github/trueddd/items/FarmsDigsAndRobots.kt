package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.FarmsDigsAndRobots}")
class FarmsDigsAndRobots private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = FarmsDigsAndRobots(uid = generateWheelItemUid())
    }

    override val id = Id(FarmsDigsAndRobots)

    override val name = "Фермы, раскопки и роботы"

    override val description = """
        Следующая игра рероллится до тех пор, пока не выпадет игра про роботов, фермеров, археологов или животных.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(pendingEvents = playerState.pendingEvents.without(uid))
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(FarmsDigsAndRobots)
        override fun create() = Companion.create()
    }
}
