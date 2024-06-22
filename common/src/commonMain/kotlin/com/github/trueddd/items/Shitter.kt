package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.items.Shitter.Debuff
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
@SerialName("${WheelItem.Shitter}")
class Shitter private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = Shitter(uid = generateWheelItemUid())
    }

    override val id = Id(Shitter)

    override val name = "Подсеруха"

    override val description = """
        |Стример, наролливший этот пункт получает дебафф в виде минуса к итоговому значению кубика в зависимости 
        |от его текущего положения на карте. Если стример занимает лидирующее место - `-3` к итоговому значению кубика, 
        |и `-2` если стример занимает второе место и `-1` если стример занимает третье место. 
        |Если стример занимает место ниже 4, то колесо рероллится.
    """.removeTabs()

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        val everyoneEqualized = globalState.stateSnapshot.playersState.values
            .map { it.position }.distinct().let { it.size == 1 }
        if (everyoneEqualized) {
            return globalState
        }
        val modifier = when (globalState.positionAmongPlayers(triggeredBy)) {
            0 -> -3
            1 -> -2
            2 -> -1
            else -> return globalState
        }.let { Debuff.create(it) }
        return globalState.updatePlayer(triggeredBy) { playerState ->
            playerState.copy(effects = playerState.effects + modifier)
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Shitter)
        override fun create() = Companion.create()
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create(modifier: Int) = Debuff(uid = generateWheelItemUid(), modifier = modifier)
        }
        override val id = Id(Shitter)
        override val name = "Подсеруха"
        override val description = """
            |`-${modifier.absoluteValue}` к броску кубика на ход.
        """.removeTabs()
    }
}
