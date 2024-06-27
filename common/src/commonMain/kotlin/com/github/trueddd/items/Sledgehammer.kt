package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.items.Sledgehammer.Debuff
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class Sledgehammer private constructor(
    override val uid: String
) : WheelItem.InventoryItem(), Parametrized<Parameters.Two<PlayerName, Boolean>> {

    companion object {
        fun create() = Sledgehammer(uid = generateWheelItemUid())
    }

    override val id = Id(Sledgehammer)

    override val name = "Кувалда"

    override val description = """
        |Позволяет разбить "Бетонную обувь" у себя или другого игрока. Предмет одноразовый. 
        |При применении бросается монетка: выпадает Решка - игрок, чью обувь пытались снять, 
        |дебафф на `-3` к следующему ходу, выпадает Орёл - обувь снимается без проблем.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(
            ParameterType.Player(
                name = "Игрок с обувью",
                predicate = { _, state -> state.effects.any { it is ConcreteBoots } }
            ),
            ParameterType.Bool(
                name = "Выпал Орёл?"
            )
        )

    override fun getParameters(
        rawArguments: List<String>,
        currentState: GlobalState
    ): Parameters.Two<PlayerName, Boolean> {
        return Parameters.Two(
            rawArguments.getParticipantParameter(index = 0, currentState)!!,
            rawArguments.getBooleanParameter(index = 1)!!,
        )
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val (bootsOwner, successfulRemoval) = getParameters(arguments, globalState)
        return globalState.updatePlayers { playerName, playerState ->
            val withoutHammer = if (playerName == usedBy) {
                playerState.copy(inventory = playerState.inventory.without(uid))
            } else {
                playerState
            }
            if (playerName == bootsOwner) {
                if (withoutHammer.effects.none { it is ConcreteBoots }) {
                    throw IllegalStateException("Player $bootsOwner doesn't have ConcreteBoots")
                }
                val effects = if (successfulRemoval) {
                    withoutHammer.effects.filter { it !is ConcreteBoots }
                } else {
                    withoutHammer.effects.filter { it !is ConcreteBoots } + Debuff.create()
                }
                withoutHammer.copy(effects = effects)
            } else {
                withoutHammer
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Sledgehammer)
        override fun create() = Companion.create()
    }

    @Serializable
    class Debuff private constructor (override val uid: String) : Effect.Debuff(), DiceRollModifier {

        companion object {

            const val MODIFIER = -3

            fun create() = Debuff(generateWheelItemUid())
        }

        override val id = Id(SledgehammerDebuff)

        override val name = "Поломанные колени"

        override val modifier: Int
            get() = MODIFIER

        override val description = """
            |Кое-кто не умеет пользоваться кувалдой, теперь у вас сломаны колени. 
            |`$MODIFIER` на следующий ход.
        """.removeTabs()
    }
}
