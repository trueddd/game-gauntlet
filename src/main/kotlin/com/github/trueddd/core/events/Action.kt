package com.github.trueddd.core.events

import com.github.trueddd.utils.DateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
sealed class Action(
    open val id: Int,
    @Serializable(with = DateSerializer::class)
    val issuedAt: Date = Date(),
    @Transient
    val singleShot: Boolean = false,
) {

    object Keys {
        const val BoardMove = 1
        const val GameDrop = 2
        const val ItemReceive = 3
    }
}
