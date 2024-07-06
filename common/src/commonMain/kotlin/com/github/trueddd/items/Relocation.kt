package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.map.Genre
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Relocation}")
class Relocation private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Genre>> {

    companion object {
        fun create() = Relocation(uid = generateWheelItemUid())
    }

    override val id = Id(Relocation)

    override val name = "Релокейшен"

    override val description = """
        |Стример роллит список жанров и переходит на ближайший сектор с выпавшим жанром.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Genre(name = "Жанр"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Genre> {
        return Parameters.One(rawArguments.getIntParameter().let { Genre.entries[it] })
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        val genre = getParameters(arguments, globalState).parameter1
        require(genre != Genre.Special)
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                position = globalState.closestPositionToGenre(playerState.position, genre),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Relocation)
        override fun create() = Companion.create()
    }
}
