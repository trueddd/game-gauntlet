package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

/**
 * Wheel item is an item that can be rolled on the wheel and somehow affect player state.
 * It can be an inventory item (that can be used later), an effect or a one-time event (that immediately affects state).
 * @property uid is an unique identifier for each item
 * @property id is an identifier of item - two items of the same type will have the same id
 */
@Serializable
sealed class WheelItem {

    abstract val uid: String
    abstract val id: Id
    abstract val name: String

    interface Factory {

        companion object {
            const val SetTag = "ItemFactory"
        }

        fun create(): WheelItem
    }

    @Serializable
    @JvmInline
    value class Id(val value: Int) {
        companion object {
            val PowerThrow = Id(1)
            val WeakThrow = Id(2)
            val YouDoNotNeedThis = Id(3)
            val SamuraiLunge = Id(4)
            val DropReverse = Id(5)
        }
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? WheelItem)?.id && uid == (other as? WheelItem)?.uid
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + id.value
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "(${id.value}-$uid/$name)"
    }

    @Serializable
    sealed class InventoryItem : WheelItem(), Usable

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
