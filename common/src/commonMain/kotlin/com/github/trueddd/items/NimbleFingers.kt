package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class NimbleFingers private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.Two<Participant, String>> {

    companion object {
        fun create() = NimbleFingers(uid = generateWheelItemUid())
    }

    override val id = Id.NimbleFingers

    override val name = "Ловкие пальцы"

    override val description = """
        Участник, наролливший этот пункт может своровать любой предмет у одного из участников ивента 
        по своему усмотрению. Если ни у одного из участников на данный момент нет предметов, 
        то данный пункт сбрасывается.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(
            ParameterType.Player(name = "Игрок"),
            ParameterType.Item(name = "target item ID", itemSetType = ItemSetType.Foreign),
        )

    override fun getParameters(
        rawArguments: List<String>,
        currentState: GlobalState
    ): Parameters.Two<Participant, String> {
        return Parameters.Two(
            rawArguments.getParticipantParameter(index = 0, currentState)!!,
            rawArguments.getStringParameter(index = 1),
        )
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val parameters = getParameters(arguments, globalState)
        val targetUser = parameters.parameter1
        val targetItem = globalState.inventoryOf(targetUser).firstOrNull { it.uid == parameters.parameter2 }
            ?: throw IllegalArgumentException("ID of target item must be specified")
        return globalState.updatePlayers { participant, state ->
            when (participant) {
                usedBy -> state.copy(
                    pendingEvents = state.pendingEvents.without(uid),
                    inventory = state.inventory + targetItem,
                )
                targetUser -> state.copy(
                    inventory = state.inventory.without(targetItem.uid),
                )
                else -> state
            }
        }
    }

    fun canUse(user: Participant, globalState: GlobalState): Boolean {
        return globalState.players.any { (player, state) -> player != user && state.inventory.isNotEmpty() }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.NimbleFingers
        override fun create() = Companion.create()
    }
}
