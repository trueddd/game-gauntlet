package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.BananaSkin}")
class BananaSkin private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        const val STEPS_BACK = 2
        fun create() = BananaSkin(uid = generateWheelItemUid())
    }

    override val id = Id(BananaSkin)

    override val name = "Банановая кожура"

    override val description = """
        После прохождения игры на текущем секторе стример может пометить этот сектор, где он только что стоял. 
        Стример, наступивший на этот сектор, автоматически отступит на два сектора назад, а кожура пропадёт. 
        Данный эффект работает и на стримера, который использовал этот предмет.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(inventory = playerState.inventory.without(uid))
        }.let { state ->
            val trapEntry = globalState.stateOf(usedBy).position to Trap()
            state.copy(
                stateSnapshot = state.stateSnapshot.copy(
                    boardTraps = state.stateSnapshot.boardTraps + trapEntry
                )
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(BananaSkin)
        override fun create() = Companion.create()
    }

    class Trap : BoardTrap {
        override val name = "Банановая кожура"
    }
}
