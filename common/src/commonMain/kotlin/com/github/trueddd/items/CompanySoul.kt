package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.CompanySoul}")
class CompanySoul private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.Two<PlayerName, Boolean>> {

    companion object {
        fun create() = CompanySoul(uid = generateWheelItemUid())
    }

    override val id = Id(CompanySoul)

    override val name = "Душа компании"

    override val description = """
        |Участник, наролливший этот пункт, идет в войс к любому другому участнику на свой выбор, 
        |и пытается его рассмешить любыми способами за 5 минут. Если рассмешить не получилось, 
        |шутник получает `-1` к следующему броску кубика, а стойко выдержавший шутки стример - `+1` к следующему 
        |броску кубика. А если удалось рассмешить - `+1` к следующему броску кубика шутнику, 
        |`-1` к следующему броску кубика стримеру, не выдержевшему напора острых шуток.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(
            ParameterType.Player(name = "Слушатель"),
            ParameterType.Bool(name = "Шутка удалась")
        )

    override fun getParameters(
        rawArguments: List<String>,
        currentState: GlobalState
    ): Parameters.Two<PlayerName, Boolean> {
        return Parameters.Two(
            rawArguments.getParticipantParameter(index = 0, currentState)!!,
            rawArguments.getBooleanParameter(index = 1)!!
        )
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val parameters = getParameters(arguments, globalState)
        val listener = parameters.parameter1
        val jokeSucceeded = parameters.parameter2
        return globalState.updatePlayers { playerName, playerState ->
            when (playerName) {
                usedBy -> playerState.copy(
                    pendingEvents = playerState.pendingEvents.without(uid),
                    effects = playerState.effects + if (jokeSucceeded) JokerBuff.create() else JokerDebuff.create()
                )
                listener -> playerState.copy(
                    effects = playerState.effects + if (jokeSucceeded) ListenerDebuff.create() else ListenerBuff.create()
                )
                else -> playerState
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(CompanySoul)
        override fun create() = Companion.create()
    }

    @Serializable
    class JokerBuff private constructor(
        override val uid: String,
        override val modifier: Int = 1,
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create() = JokerBuff(uid = generateWheelItemUid())
        }
        override val id = Id(CompanySoul)
        override val name = "Вы - Душа компании"
        override val description = "`+1` к следующему броску кубика на ход."
    }

    @Serializable
    class ListenerBuff private constructor(
        override val uid: String,
        override val modifier: Int = 1,
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create() = ListenerBuff(uid = generateWheelItemUid())
        }
        override val id = Id(CompanySoul)
        override val name = "Зануда"
        override val description = "Попробуй рассмеши тебя - `+1` к следующему броску кубика на ход."
    }

    @Serializable
    class JokerDebuff private constructor(
        override val uid: String,
        override val modifier: Int = -1,
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create() = JokerDebuff(uid = generateWheelItemUid())
        }
        override val id = Id(CompanySoul)
        override val name = "Вы не Душа компании"
        override val description = "Следующий раз ищи анекдоты посмешнее - `-1` к следующему броску кубика на ход."
    }

    @Serializable
    class ListenerDebuff private constructor(
        override val uid: String,
        override val modifier: Int = -1,
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create() = ListenerDebuff(uid = generateWheelItemUid())
        }
        override val id = Id(CompanySoul)
        override val name = "Весельчак"
        override val description = "Посмеялись - и хватит. `-1` к следующему броску кубика на ход."
    }
}
