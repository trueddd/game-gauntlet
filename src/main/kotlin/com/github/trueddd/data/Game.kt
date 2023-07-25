package com.github.trueddd.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Game(
    val id: Id,
    val name: String,
    val link: String? = null,
) {

    @Serializable
    @JvmInline
    value class Id(val value: Int)

    @Serializable
    enum class Status {
        @SerialName("in_progress")
        InProgress,
        @SerialName("finished")
        Finished,
        @SerialName("dropped")
        Dropped,
        @SerialName("rerolled")
        Rerolled;

        val isComplete: Boolean
            get() = this != InProgress

        val allowsNextStep: Boolean
            get() = this == Finished || this == Dropped
    }
}
