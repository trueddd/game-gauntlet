package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class RatMove private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Participant?>> {

    companion object {
        fun create() = RatMove(uid = generateWheelItemUid())
    }

    override val id = Id.RatMove

    override val name = "Крысиный поступок"

    override val description = """
        Выбери стримера и сбрось его инвентарь, баффы и дебаффы. Теперь ты главная крыса ивента. 
        Нельзя сбросить пустой инвентарь, нельзя сбросить свой инвентарь, если у всех стримеров пустой инвентарь, 
        то пункт рероллится.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Player(name = "Стример"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Participant?> {
        return Parameters.One(rawArguments.getParticipantParameter(index = 0, currentState, optional = true))
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val targetUser = getParameters(arguments, globalState).parameter1 ?: usedBy
        return globalState.updatePlayers { participant, playerState ->
            when (participant.name) {
                usedBy.name -> playerState.copy(pendingEvents = playerState.pendingEvents.without(uid))
                targetUser.name -> if (targetUser != usedBy) {
                    playerState.copy(
                        inventory = emptyList(),
                        effects = playerState.effects.filterIsInstance<EasterCakeBang>(),
                    )
                } else {
                    playerState
                }
                else -> playerState
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.RatMove
        override fun create() = Companion.create()
    }
}
