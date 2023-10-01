package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class UnbelievableDemocracy private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = UnbelievableDemocracy(uid = generateWheelItemUid())
    }

    override val id = Id.UnbelievableDemocracy

    override val name = "Невероятная демократия"

    override val description = """
        Чат решает плюс очко или минус очко к следующему броску кубика для всех участников.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val pollSucceeded = arguments.getBooleanParameter()
        return globalState.updatePlayers { participant, playerState ->
            playerState.copy(
                pendingEvents = if (participant == usedBy) {
                    playerState.pendingEvents.without(uid)
                } else {
                    playerState.pendingEvents
                },
                effects = playerState.effects + if (pollSucceeded) Buff.create(usedBy) else Debuff.create(usedBy),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.UnbelievableDemocracy
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int = 1,
        val causedByPollFrom: Participant
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create(causedByPollFrom: Participant) = Buff(
                uid = generateWheelItemUid(),
                causedByPollFrom = causedByPollFrom
            )
        }
        override val id = Id.UnbelievableDemocracy
        override val name = "Невероятная демократия"
        override val description = """
            +${modifier.absoluteValue} к броску кубика на ход. Спасибо чату ${causedByPollFrom.name} за это.
        """.trimIndent()
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int = -1,
        val causedByPollFrom: Participant
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create(causedByPollFrom: Participant) = Debuff(
                uid = generateWheelItemUid(),
                causedByPollFrom = causedByPollFrom
            )
        }
        override val id = Id.UnbelievableDemocracy
        override val name = "Невероятная демократия"
        override val description = """
            -${modifier.absoluteValue} к броску кубика на ход. Спасибо чату ${causedByPollFrom.name} за это.
        """.trimIndent()
    }
}
