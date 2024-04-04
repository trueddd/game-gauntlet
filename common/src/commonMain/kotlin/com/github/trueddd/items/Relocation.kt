package com.github.trueddd.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class Relocation private constructor(override val uid: String) : WheelItem.PendingEvent(),
    Parametrized<Parameters.One<Game.Genre>> {

    companion object {
        fun create() = Relocation(uid = generateWheelItemUid())
    }

    override val id = Id.Relocation

    override val name = "Релокейшен"

    override val description = """
        Стример роллит список жанров и переходит на ближайший сектор с выпавшим жанром.
    """.trimIndent()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.Genre(name = "Жанр"))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<Game.Genre> {
        return Parameters.One(rawArguments.getIntParameter().let { Game.Genre.entries[it] })
    }

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val genre = getParameters(arguments, globalState).parameter1
        require(genre != Game.Genre.Special)
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                position = globalState.gameGenreDistribution.closestPositionToGenre(playerState.position, genre),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.Relocation
        override fun create() = Companion.create()
    }
}
