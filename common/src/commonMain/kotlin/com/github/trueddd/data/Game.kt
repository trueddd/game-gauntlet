package com.github.trueddd.data

import com.github.trueddd.map.Genre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class Game(
    val id: Id,
    override val name: String,
    val genre: Genre,
): Rollable {

    override val description: String
        get() = name

    override val color: Long
        get() = genre.color

    @JvmInline
    @Serializable
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
        Rerolled,
        @SerialName("next")
        Next;

        val isComplete: Boolean
            get() = this != InProgress && this != Next

        val allowsNextStep: Boolean
            get() = this == Finished || this == Dropped
    }
}
