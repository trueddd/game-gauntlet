package com.github.trueddd.data.items

import com.github.trueddd.data.Game
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class Relocation private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = Relocation(uid = generateWheelItemUid())
    }

    override val id = Id.Relocation

    override val name = "Релокейшен"

    override val description = """
        Стример роллит список жанров и переходит на ближайший сектор с выпавшим жанром.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val genre = arguments.firstOrNull()?.toIntOrNull()
            ?.let { Game.Genre.entries.getOrNull(it) }
            ?.also { require(it != Game.Genre.Special) }
            ?: throw IllegalArgumentException("Index of genre must be specified")
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
        override fun create() = Relocation.create()
    }
}
