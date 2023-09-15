package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class Democracy private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = Democracy(uid = generateWheelItemUid())
    }

    override val id = Id.Democracy

    override val name = "Демократия"

    override val description = """
        Чат решает плюс очко или минус очко к следующему броску кубика.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val pollSucceeded = arguments.firstOrNull().let {
            when (it) {
                "1" -> true
                "0" -> false
                else -> throw IllegalArgumentException("Boolean value must be specified as a poll result, but was $it")
            }
        }
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + if (pollSucceeded) Buff.create() else Debuff.create(),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.Democracy
        override fun create() = Democracy.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int = 1
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create() = Buff(uid = generateWheelItemUid())
        }
        override val id = Id.Democracy
        override val name = "Демократия"
        override val description = "+${modifier.absoluteValue} к броску кубика на ход."
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int = -1
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create() = Debuff(uid = generateWheelItemUid())
        }
        override val id = Id.Democracy
        override val name = "Демократия"
        override val description = "-${modifier.absoluteValue} к броску кубика на ход."
    }
}
