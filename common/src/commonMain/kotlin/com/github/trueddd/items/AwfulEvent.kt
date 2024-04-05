package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.items.AwfulEvent.Debuff
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
@SerialName("${WheelItem.AwfulEvent}")
class AwfulEvent private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Int>> {

    companion object {
        fun create() = AwfulEvent(uid = generateWheelItemUid())
    }

    override val id = Id(AwfulEvent)

    override val name = "Плохой ивент"

    override val description = """
        Отними от следующего броска кубика значение в зависимости от количества стримящих в данный момент участников.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Int(name = "Количество стримящих участников"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Int> {
        return Parameters.One(rawArguments.getIntParameter())
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val online = getParameters(arguments, globalState).parameter1
        require(online in 0..globalState.players.size)
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + Debuff.create(online),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(AwfulEvent)
        override fun create() = Companion.create()
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create(modifier: Int) = Debuff(uid = generateWheelItemUid(), modifier = -modifier)
        }

        override val id = Id(AwfulEvent)
        override val name = "Плохой ивент"
        override val description = """
            -${modifier.absoluteValue} к броску кубика на ход. Спасибо всем, кто был онлайн.
        """.trimIndent()
    }
}
