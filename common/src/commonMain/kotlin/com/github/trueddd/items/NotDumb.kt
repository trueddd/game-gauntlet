package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.NotDumb}")
class NotDumb private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Int>> {

    companion object {
        fun create() = NotDumb(uid = generateWheelItemUid())
    }

    override val id = Id(NotDumb)

    override val name = "Стример не тупой"

    override val description = """
        Стример открывает сайт https://randstuff.ru/question/ и отвечает на 7 случайных вопросов. 
        Если стример ответил на 2 вопроса или меньше, он получает -3 к следующему броску кубика для перехода по секторам, 
        3 - -2, 4 - -1, 5 - +1, 6 - +2, 7 - +3. Правильный ответ на красный вопрос считается за два правильных ответа. 
        На время ответов на вопросы чат у стримера переходит в смайл мод, а если какой-то модератор или VIP-пользователь 
        дает подсказку - ответ не засчитывается, вне зависимости от того, смотрел стример в чат или нет. 
        Пользоваться любыми способами получения информации на время прохождения теста стримеру запрещено. 
        Если один и тот же вопрос выпадает повторно, стример должен его реролльнуть.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Int(name = "Количество правильных ответов"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Int> {
        return Parameters.One(rawArguments.getIntParameter())
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val rightAnswers = getParameters(arguments, globalState).parameter1
        require(rightAnswers in 0..7) {
            "Result should be in range from 0 to 7, but was $rightAnswers"
        }
        val value = when (rightAnswers) {
            0, 1, 2 -> -3
            3 -> -2
            4 -> -1
            5 -> 1
            6 -> 2
            7 -> 3
            else -> return globalState
        }
        val modifier = if (value >= 0) {
            Buff.create(value)
        } else {
            Debuff.create(value)
        }
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + modifier,
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(NotDumb)
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Buff(), DiceRollModifier {

        companion object {
            fun create(modifier: Int) = Buff(uid = generateWheelItemUid(), modifier)
        }

        override val id = Id(NotDumb)

        override val name = "Стример не тупой"

        override val description = "Вы доказали силу своего разума - +$modifier к следующему броску кубика на ход."
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Buff(), DiceRollModifier {

        companion object {
            fun create(modifier: Int) = Debuff(uid = generateWheelItemUid(), modifier)
        }

        override val id = Id(NotDumb)

        override val name = "Стример не тупой (?)"

        override val description = "Надо было больше книжек читать - -$modifier к следующему броску кубика на ход."
    }
}
