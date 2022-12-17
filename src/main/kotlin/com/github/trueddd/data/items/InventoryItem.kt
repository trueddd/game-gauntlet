package com.github.trueddd.data.items

import kotlinx.serialization.Serializable

@Serializable
sealed class InventoryItem {

    abstract val uid: Long
    abstract val id: Int
    abstract val name: String

    abstract class Factory {

        companion object {
            const val SET_NAME = "ItemFactory"
        }

        abstract fun create(): InventoryItem
    }

    object Id {
        const val PowerThrow = 1
        const val WeakThrow = 2
        const val YouDoNotNeedThis = 3
        const val SamuraiLunge = 4
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? InventoryItem)?.id && uid == (other as? InventoryItem)?.uid
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
    sealed class Item : InventoryItem(), Usable {
        abstract val maxChargesAmount: Int
        abstract val chargesAmount: Int
    }

    @Serializable
    sealed class Event : InventoryItem()

    @Serializable
    sealed class Effect : InventoryItem() {

        @Serializable
        sealed class Buff : Effect()

        @Serializable
        sealed class Debuff : Effect()
    }
}
