package com.github.trueddd.items

import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.utils.getItemFactoriesSet
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Poll}")
class Poll private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.Two<Int, PlayerName?>> {

    companion object {
        fun create() = Poll(uid = generateWheelItemUid())
    }

    override val id = Id(Poll)

    override val name = "Голосование"

    override val description = """
        |Дополнительно прокрути колесо, после чего проведи голосование что делать с выпавшим пунктом. 
        |Первое голосование - оставить себе или кинуть в другого участника. 
        |В случае, если в голосовании победило "кинуть в другого участника", проведи дополнительное голосование 
        |в какого именно участника (проводится между теми участниками, кто сейчас в онлайне, если никого в онлайне нет, 
        |то среди всех).
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(
            ParameterType.Item(name = "Выпавший пункт"),
            ParameterType.Player(name = "Получатель"),
        )

    override fun getParameters(
        rawArguments: List<String>,
        currentState: GlobalState
    ): Parameters.Two<Int, PlayerName?> {
        return Parameters.Two(
            rawArguments.getIntParameter(index = 0),
            rawArguments.getParticipantParameter(index = 1, currentState, optional = true)
        )
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val parameters = getParameters(arguments, globalState)
        val itemId = parameters.parameter1
        val receiver = parameters.parameter2 ?: usedBy
        val factory = getItemFactoriesSet().firstOrNull { it.itemId.value == itemId }
            ?: throw IllegalArgumentException("No factory found for itemId $itemId")
        val action = ItemReceive(receiver, factory.create())
        return globalState.updatePlayer(usedBy) { it.copy(pendingEvents = it.pendingEvents.without(uid)) }
            .let { ItemReceive.Handler().handle(action, it) }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Poll)
        override fun create() = Companion.create()
    }
}
