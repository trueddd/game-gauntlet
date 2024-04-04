package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.items.DiceBattle.Buff
import com.github.trueddd.items.DiceBattle.Debuff
import com.github.trueddd.utils.d6Range
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class DiceBattle private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.Three<Participant, Int, Int>> {

    companion object {
        fun create() = DiceBattle(uid = generateWheelItemUid())
    }

    override val id = Id.DiceBattle

    override val name = "Писькомерянье"

    override val description = """
        Наролливший стример рандомно выбирает другого стримера из тех кто сейчас находится в онлайне 
        (если никого сейчас нет онлайн - реролл колеса) и созванивается в дискорде. Затем оба стримера кидают кубик d6. 
        Тот у кого число больше получает плюс столько очков сколько на кубике, а тот у кого меньше - 
        получает количество минус очков своего кубика. При выпавших одинаковых числах, кубики бросаются еще раз.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(
            ParameterType.Player("Соперник"),
            ParameterType.Int("Мой кубик"),
            ParameterType.Int("Кубик соперника"),
        )

    override fun getParameters(
        rawArguments: List<String>,
        currentState: GlobalState
    ): Parameters.Three<Participant, Int, Int> {
        return Parameters.Three(
            rawArguments.getParticipantParameter(index = 0, currentState)!!,
            rawArguments.getIntParameter(index = 1),
            rawArguments.getIntParameter(index = 2),
        )
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val parameters = getParameters(arguments, globalState)
        val opponent = parameters.parameter1
        val myDice = parameters.parameter2.takeIf { it in d6Range }
            ?: throw IllegalArgumentException("Player dice value was corrupted or not specified")
        val opponentDice = parameters.parameter3.takeIf { it in d6Range }
            ?: throw IllegalArgumentException("Opponent dice value was corrupted or not specified")
        if (myDice == opponentDice) {
            throw IllegalArgumentException("Dice values must differ")
        }
        val myEffect = if (myDice > opponentDice) Buff.create(myDice) else Debuff.create(-myDice)
        val opponentEffect = if (opponentDice > myDice) Buff.create(opponentDice) else Debuff.create(-opponentDice)
        return globalState.updatePlayers { participant, playerState ->
            when (participant.name) {
                usedBy.name -> playerState.copy(
                    pendingEvents = playerState.pendingEvents.without(uid),
                    effects = playerState.effects + myEffect,
                )
                opponent.name -> playerState.copy(
                    effects = playerState.effects + opponentEffect,
                )
                else -> playerState
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.DiceBattle
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create(diceValue: Int) = Buff(uid = generateWheelItemUid(), modifier = diceValue)
        }
        override val id = Id.DiceBattle
        override val name = "Писькомерянье"
        override val description = "+${modifier.absoluteValue} к броску кубика на ход"
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create(diceValue: Int) = Debuff(uid = generateWheelItemUid(), modifier = diceValue)
        }
        override val id = Id.DiceBattle
        override val name = "Писькомерянье"
        override val description = "-${modifier.absoluteValue} к броску кубика на ход"
    }
}
