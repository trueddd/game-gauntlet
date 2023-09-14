package com.github.trueddd.data.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class LuckyThrow private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = LuckyThrow(uid = generateWheelItemUid())
    }

    override val id = Id.LuckyThrow

    override val name = "Счастливый бросок"

    override val description = """
        При выпадении этого пункта стример роллит жанры и если после следующего броска кубика он попадает на клетку 
        с выпавшим жанром, то эта клетка автоматически засчитывается пройденной и стример двигается дальше.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val genre = arguments.first().let { Json.decodeFromString<Game.Genre>(it) }
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.filter { it.uid != uid },
                effects = playerState.effects + LuckyThrowBuff.create(genre),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.LuckyThrow
        override fun create() = LuckyThrow.create()
    }
}
