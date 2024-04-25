package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.items.Democracy.Buff
import com.github.trueddd.items.Democracy.Debuff
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
@SerialName("${WheelItem.Democracy}")
class Democracy private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Boolean>> {

    companion object {
        fun create() = Democracy(uid = generateWheelItemUid())
    }

    override val id = Id(Democracy)

    override val name = "Демократия"

    override val description = """
        Чат решает плюс очко или минус очко к следующему броску кубика.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Bool(name = "Чат выбрал плюс?"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Boolean> {
        return Parameters.One(rawArguments.getBooleanParameter()!!)
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val pollSucceeded = getParameters(arguments, globalState).parameter1
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + if (pollSucceeded) Buff.create() else Debuff.create(),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Democracy)
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int = 1
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create() = Buff(uid = generateWheelItemUid())
        }
        override val id = Id(Democracy)
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
        override val id = Id(Democracy)
        override val name = "Демократия"
        override val description = "-${modifier.absoluteValue} к броску кубика на ход."
    }
}
