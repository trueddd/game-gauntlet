package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

@Serializable
sealed class WheelItem {

    abstract val uid: Long
    abstract val id: Int
    abstract val name: String

    abstract class Factory {

        companion object {
            const val SET_NAME = "ItemFactory"
        }

        abstract fun create(): WheelItem
    }

    object Id {
        const val PowerThrow = 1
        const val WeakThrow = 2
        const val YouDoNotNeedThis = 3
        const val SamuraiLunge = 4
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? WheelItem)?.id && uid == (other as? WheelItem)?.uid
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + id
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "($id-$uid/$name)"
    }

    @Serializable
    sealed class InventoryItem : WheelItem(), Usable {
        abstract val maxChargesAmount: Int
        abstract val chargesAmount: Int
    }

    @Serializable
    sealed class Event : WheelItem() {
        abstract suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState
    }

    @Serializable
    sealed class Effect : WheelItem() {

        @Serializable
        sealed class Buff : Effect()

        @Serializable
        sealed class Debuff : Effect()
    }
}
