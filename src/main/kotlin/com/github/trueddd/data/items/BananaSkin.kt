package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class BananaSkin private constructor(override val uid: String) : WheelItem.InventoryItem() {

    companion object {
        fun create() = BananaSkin(uid = generateWheelItemUid())
    }

    override val id = Id.BananaSkin

    override val name = "Банановая кожура"

    override val description = """
        После прохождения игры на текущем секторе стример может пометить этот сектор, где он только что стоял. 
        Стример, наступивший на этот сектор, автоматически отступит на два сектора назад, а кожура пропадёт. 
        Данный эффект работает и на стримера, который использовал этот предмет.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(inventory = playerState.inventory.filter { it.uid != uid })
        }.let { state ->
            val trapEntry = globalState.players[usedBy]!!.position to BananaSkinTrap()
            state.copy(boardTraps = state.boardTraps + trapEntry)
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override fun create() = BananaSkin.create()
    }
}
