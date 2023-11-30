package com.github.trueddd.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Id,
    val name: String,
    val link: String? = null,
) {

    @Serializable
    data class Id(val value: Int)

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

    @Serializable
    enum class Genre {
        @SerialName("runner")
        Runner,
        @SerialName("business")
        Business,
        @SerialName("puzzle")
        Puzzle,
        @SerialName("point_and_click")
        PointAndClick,
        @SerialName("shooter")
        Shooter,
        @SerialName("three_in_row")
        ThreeInRow,
        @SerialName("special")
        Special;
    }
}
