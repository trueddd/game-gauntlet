package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.items.WillOfChance.Buff
import com.github.trueddd.items.WillOfChance.Debuff
import com.github.trueddd.utils.isEven
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.WillOfChance}")
class WillOfChance private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = WillOfChance(uid = generateWheelItemUid())
    }

    override val id = Id(WillOfChance)

    override val name = "Воля случая"

    override val description = """
        |Если сектор, на котором вы находитесь, четный - получите `+2` к следующему броску кубика 
        |для перехода по секторам. Если нечетный - `-2`.
    """.removeTabs()

    override suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState {
        return globalState.updatePlayer(triggeredBy) { playerState ->
            val effect = if (playerState.position.isEven) {
                Buff.create()
            } else {
                Debuff.create()
            }
            playerState.copy(effects = playerState.effects + effect)
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(WillOfChance)
        override fun create() = Companion.create()
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int = -2
    ) : Effect.Debuff(), DiceRollModifier {

        companion object {
            fun create() = Debuff(uid = generateWheelItemUid())
        }

        override val id = Id(WillOfBadChance)

        override val name = "Воля случая. Дебафф"

        override val description = "`-2` к следующему броску кубика для перехода по секторам."
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int = 2
    ) : Effect.Buff(), DiceRollModifier {

        companion object {
            fun create() = Buff(uid = generateWheelItemUid())
        }

        override val id = Id(WillOfGoodChance)

        override val name = "Воля случая. Бафф"

        override val description = "`+2` к следующему броску кубика для перехода по секторам."
    }
}
