package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Teleport}")
class Teleport private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Boolean?>> {

    companion object {
        fun create() = Teleport(uid = generateWheelItemUid())
    }

    override val id = Id(Teleport)

    override val name = "Телепорт"

    override val description = """
        |Позволяет телепортироваться к одному из соседей по секторам, чтобы определиться к какому соседу 
        |ты телепортируешься, бросается монетка РЕШКА - сосед сзади, ОРЕЛ - сосед спереди 
        |(те с кем ты стоишь на одном секторе, соседями не являются, стример на финишной клетке соседом не является). 
        |Если сосед только один, телепортируешься к нему, если соседей нет вовсе - УВЫ.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Bool(name = "Выпал Орёл?"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Boolean?> {
        return Parameters.One(rawArguments.getBooleanParameter(optional = true))
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val coin = getParameters(arguments, globalState).parameter1
        val userPosition = globalState.positionOf(usedBy)
        val neighborBack = globalState.stateSnapshot.playersState.values
            .filter { it.position < userPosition && it.position in GlobalState.PLAYABLE_BOARD_RANGE }
            .maxOfOrNull { it.position }
        val neighborForth = globalState.stateSnapshot.playersState.values
            .filter { it.position > userPosition && it.position < GlobalState.PLAYABLE_BOARD_RANGE.last }
            .minOfOrNull { it.position }
        when {
            neighborBack == null && neighborForth != null -> neighborForth
            neighborBack != null && neighborForth == null -> neighborBack
            neighborBack == null && neighborForth == null -> userPosition
            neighborBack != null && neighborForth != null -> when (coin!!) {
                true -> neighborForth
                false -> neighborBack
            }
            else -> throw IllegalStateException("This state is unreachable")
        }.let { newPosition ->
            return globalState.updatePlayer(usedBy) { playerState ->
                playerState.copy(
                    pendingEvents = playerState.pendingEvents.without(uid),
                    position = newPosition
                )
            }
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Teleport)
        override fun create() = Companion.create()
    }
}
