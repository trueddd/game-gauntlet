package com.github.trueddd.data.items

import kotlinx.serialization.Serializable

@Serializable
class DropReverse private constructor(override val uid: Long) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = DropReverse(uid = System.currentTimeMillis())
    }

    override val id = Id.DropReverse

    override val name = "Drop penalty reverse"
}
