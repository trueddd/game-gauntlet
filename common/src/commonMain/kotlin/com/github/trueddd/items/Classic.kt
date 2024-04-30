package com.github.trueddd.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Classic}")
class Classic private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = Classic(uid = generateWheelItemUid())
        val TargetGameIds = listOf(Game.Id(5)) // TODO: replace with actual games
    }

    override val id = Id(Classic)

    override val name = "Классика"

    override val description = """
        |Если на этом ивенте одному из участников выпадала "Супер Корова" или одна из частей "Веселой Фермы", 
        |то все участники получают `+1` к следующему броску кубика, иначе ничего не проиходит.
    """.removeTabs()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return if (globalState.getAllEverRolledGames().any { it.id in TargetGameIds }) {
            globalState.updatePlayers { _, playerState ->
                playerState.copy(
                    effects = playerState.effects + Buff.create()
                )
            }
        } else {
            globalState
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Classic)
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
        override val id = Id(Classic)
        override val name = "Классика"
        override val description = "+1 к следующему броску кубика"
    }
}
