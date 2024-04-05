package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
@SerialName("${WheelItem.UnrecognizedDisk}")
class UnrecognizedDisk private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Boolean>> {

    companion object {
        fun create() = UnrecognizedDisk(uid = generateWheelItemUid())
    }

    override val id = Id(UnrecognizedDisk)

    override val name = "Неопознанная дискета"

    override val description = """
        Вместо наролленой (не начатой) игры ролль спецколесо из игр детства. 
        Если игра пройдена за один стрим, получи +1 к следующему броску кубика.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Bool(name = "Пройдено за один стрим?"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Boolean> {
        return Parameters.One(rawArguments.getBooleanParameter()!!)
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val completedWithinSingleStream = getParameters(arguments, globalState).parameter1
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = if (completedWithinSingleStream) playerState.effects + Buff.create() else playerState.effects
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(UnrecognizedDisk)
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

        override val id = Id(UnrecognizedDisk)
        override val name = "Неопознанная дискета"
        override val description = "+${modifier.absoluteValue} к следующему броску кубика для перехода по секторам."
    }
}
