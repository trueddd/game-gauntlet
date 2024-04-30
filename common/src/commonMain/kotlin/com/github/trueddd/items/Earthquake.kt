package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.isEven
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.Earthquake}")
class Earthquake private constructor(override val uid: String) : WheelItem.Event() {

    companion object {
        fun create() = Earthquake(uid = generateWheelItemUid())
    }

    override val id = Id(Earthquake)

    override val name = "Землетрясение"

    override val description = """
        |Действует на всех. Игроки, стоящие на нечетных клетках, перемещаются на одну клетку назад. 
        |Игроки, стоящие на четных клетках, перемещаются на одну клетку вперед.
    """.removeTabs()

    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState.updatePlayers { _, playerState ->
            playerState.copy(
                position = when (playerState.position.isEven) {
                    true -> playerState.position + 1
                    false -> playerState.position - 1
                }.coerceIn(GlobalState.PLAYABLE_BOARD_RANGE)
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(Earthquake)
        override fun create() = Companion.create()
    }
}
