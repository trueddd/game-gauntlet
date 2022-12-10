package com.github.trueddd.data.items

import kotlinx.serialization.Serializable

@Serializable
sealed class InventoryItem {

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
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? InventoryItem)?.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "($id/$name)"
    }

    @Serializable
    sealed class Item(
        val maxChargesAmount: Int,
        val chargesAmount: Int,
    ) : InventoryItem()

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
