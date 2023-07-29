package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import kotlinx.serialization.Serializable

@Serializable
class DropReverse private constructor(override val uid: String) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = DropReverse(uid = generateWheelItemUid())
    }

    override val id = Id.DropReverse

    override val name = "Drop penalty reverse"
}
