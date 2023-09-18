package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class GreatEvent private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = GreatEvent(uid = generateWheelItemUid())
    }

    override val id = Id.GreatEvent

    override val name = "Крутой ивент"

    override val description = """
        К следующему броску кубика прибавь значение в зависимости от количества стримящих в данный момент участников.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val online = arguments.firstOrNull()?.toIntOrNull()
            ?.also { require(it in 0 .. globalState.players.size) }
            ?: throw IllegalArgumentException("Amount of online players must be specified, but was ${arguments.firstOrNull()}")
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + Buff.create(online),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.GreatEvent
        override fun create() = GreatEvent.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create(modifier: Int) = Buff(uid = generateWheelItemUid(), modifier = modifier)
        }
        override val id = Id.GreatEvent
        override val name = "Крутой ивент"
        override val description = """
            +${modifier.absoluteValue} к броску кубика на ход. Спасибо всем, кто был онлайн.
        """.trimIndent()
    }
}
