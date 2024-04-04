package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class ThereIsGiftAtYourDoor private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Participant?>> {

    companion object {
        fun create() = ThereIsGiftAtYourDoor(uid = generateWheelItemUid())
    }

    override val id = Id.ThereIsGiftAtYourDoor

    override val name = "\"У вас под дверью насрано\""

    override val description = """
        После прохождения текущий игры участник с этим дебаффом остается на том же секторе. 
        Можно кинуть на другого участника, но в таком случае кидающий дебафф участник получает -2 
        к следующему броску кубика.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Player(name = "Участник", optional = true))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Participant?> {
        return Parameters.One(rawArguments.getParticipantParameter(index = 0, currentState, optional = true))
    }

    override suspend fun use(
        usedBy: Participant,
        globalState: GlobalState,
        arguments: List<String>
    ): GlobalState {
        return when (val target = getParameters(arguments, globalState).parameter1 ?: usedBy) {
            usedBy -> globalState.updatePlayer(usedBy) { playerState ->
                playerState.copy(
                    effects = playerState.effects + StayAfterGame.create(),
                    pendingEvents = playerState.pendingEvents.without(uid)
                )
            }
            else -> globalState.updatePlayers { participant, playerState ->
                when (participant) {
                    target -> playerState.copy(
                        effects = playerState.effects + StayAfterGame.create()
                    )
                    usedBy -> playerState.copy(
                        effects = playerState.effects + Debuff.create(),
                        pendingEvents = playerState.pendingEvents.without(uid)
                    )
                    else -> playerState
                }
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.ThereIsGiftAtYourDoor
        override fun create() = Companion.create()
    }

    @Serializable
    class StayAfterGame private constructor(
        override val uid: String
    ) : Effect.Debuff() {
        companion object {
            fun create() = StayAfterGame(uid = generateWheelItemUid())
        }
        override val id = Id.ThereIsGiftAtYourDoor
        override val name = "\"У вас под дверью насрано\""
        override val description = "Вам насрали под дверь. После прохождения игры вы остаётесь на текущей клетке."
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int = -2
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create() = Debuff(uid = generateWheelItemUid())
        }
        override val id = Id.ThereIsGiftAtYourDoor
        override val name = "\"У вас под дверью насрано\""
        override val description = "-2 к следующему броску кубика для перехода по секторам. Серун..."
    }
}
