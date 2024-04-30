package com.github.trueddd.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.items.LuckyThrow.Buff
import com.github.trueddd.utils.removeTabs
import com.github.trueddd.utils.serialization
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.LuckyThrow}")
class LuckyThrow private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Game.Genre>> {

    companion object {
        fun create() = LuckyThrow(uid = generateWheelItemUid())
    }

    override val id = Id(LuckyThrow)

    override val name = "Счастливый бросок"

    override val description = """
        |При выпадении этого пункта стример роллит жанры и если после следующего броска кубика он попадает на клетку 
        |с выпавшим жанром, то эта клетка автоматически засчитывается пройденной и стример двигается дальше.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Genre(name = "Жанр"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Game.Genre> {
        return Parameters.One(rawArguments.getStringParameter().let { serialization.decodeFromString(it) })
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val genre = getParameters(arguments, globalState).parameter1
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + Buff.create(genre),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(LuckyThrow)
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        val genre: Game.Genre
    ) : Effect.Buff() {

        companion object {
            fun create(genre: Game.Genre) = Buff(uid = generateWheelItemUid(), genre)
        }

        override val id = Id(LuckyThrow)

        override val name = "Счастливый бросок. ${genre.name}"

        override val description = """
            |Если после следующего броска кубика стример попадает на клетку с жанром ${genre.name}, 
            |то эта клетка автоматически засчитывается пройденной и стример двигается дальше.
        """.removeTabs()
    }
}
