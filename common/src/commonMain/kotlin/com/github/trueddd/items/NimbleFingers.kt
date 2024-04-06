package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.NimbleFingers}")
class NimbleFingers private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<String>> {

    companion object {
        fun create() = NimbleFingers(uid = generateWheelItemUid())
    }

    override val id = Id(NimbleFingers)

    override val name = "Ловкие пальцы"

    override val description = """
        Участник, наролливший этот пункт может своровать любой предмет у одного из участников ивента 
        по своему усмотрению. Если ни у одного из участников на данный момент нет предметов, 
        то данный пункт сбрасывается.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(
            ParameterType.ForeignItem(name = "Предмет", predicate = { it is InventoryItem }),
        )

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<String> {
        return Parameters.One(rawArguments.getStringParameter())
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val parameters = getParameters(arguments, globalState)
        val targetItemId = parameters.parameter1
        val targetUser = globalState.stateSnapshot.playersState.firstNotNullOfOrNull { (player, state) ->
            if (state.inventory.any { it.uid == targetItemId }) {
                globalState.players.firstOrNull { it.name == player }
            } else {
                null
            }
        } ?: throw IllegalArgumentException("Not found target user")
        val targetItem = globalState.inventoryOf(targetUser).firstOrNull { it.uid == parameters.parameter1 }
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
        return globalState.stateSnapshot.playersState.any { (player, state) ->
            player != user.name && state.inventory.isNotEmpty()
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(NimbleFingers)
        override fun create() = Companion.create()
    }
}
