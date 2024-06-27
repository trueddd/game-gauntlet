package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
@SerialName("${WheelItem.GreatEvent}")
class GreatEvent private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Int>> {

    companion object {
        fun create() = GreatEvent(uid = generateWheelItemUid())
    }

    override val id = Id(GreatEvent)

    override val name = "Крутой ивент"

    override val description = """
        |К следующему броску кубика прибавь значение в зависимости от количества стримящих в данный момент участников.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Int(name = "Количество стримящих"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Int> {
        return Parameters.One(rawArguments.getIntParameter())
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val online = getParameters(arguments, globalState).parameter1
        require(online in 0 .. globalState.players.size)
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + Buff.create(online),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(GreatEvent)
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create(modifier: Int) = Buff(uid = generateWheelItemUid(), modifier = modifier)
        }
        override val id = Id(GreatEvent)
        override val name = "Крутой ивент"
        override val description = """
            |`+${modifier.absoluteValue}` к броску кубика на ход. Спасибо всем, кто был онлайн.
        """.removeTabs()
    }
}
