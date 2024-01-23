package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class Teleport private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = Teleport(uid = generateWheelItemUid())
    }

    override val id = Id.Teleport

    override val name = "Телепорт"

    override val description = """
        Позволяет телепортироваться к одному из соседей по секторам, чтобы определиться к какому соседу 
        ты телепортируешься, бросается монетка РЕШКА - сосед сзади, ОРЕЛ - сосед спереди 
        (те с кем ты стоишь на одном секторе, соседями не являются, стример на финишной клетке соседом не является). 
        Если сосед только один, телепортируешься к нему, если соседей нет вовсе - УВЫ.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val userPosition = globalState.positionOf(usedBy)
        val neighborBack = globalState.players.values
            .filter { it.position < userPosition && it.position in GlobalState.PLAYABLE_BOARD_RANGE }
            .maxOfOrNull { it.position }
        val neighborForth = globalState.players.values
            .filter { it.position > userPosition && it.position < GlobalState.PLAYABLE_BOARD_RANGE.last }
            .minOfOrNull { it.position }
        when {
            neighborBack == null && neighborForth != null -> neighborForth
            neighborBack != null && neighborForth == null -> neighborBack
            neighborBack == null && neighborForth == null -> userPosition
            neighborBack != null && neighborForth != null -> when (arguments.getBooleanParameter(0)) {
                true -> neighborForth
                false -> neighborBack
            }
            else -> throw IllegalStateException("This state is unreachable")
        }.let { newPosition ->
            return globalState.updatePlayer(usedBy) { playerState ->
                playerState.copy(
                    pendingEvents = playerState.pendingEvents.without(uid),
                    position = newPosition
                )
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.Teleport
        override fun create() = Companion.create()
    }
}
