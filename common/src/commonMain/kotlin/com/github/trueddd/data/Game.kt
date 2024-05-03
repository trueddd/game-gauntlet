package com.github.trueddd.data

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
        @SerialName("special") // TODO: fill up special games
        Special;

        val color: Long
            get() = when (this) {
                Runner -> 0xFFF87171
                Business -> 0xFF60A5FA
                Puzzle -> 0xFFA78BFA
                PointAndClick -> 0xFF4ADE80
                Shooter -> 0xFFFBBF24
                ThreeInRow -> 0xFFFB923C
                Special -> 0xFF9CA3AF
            }
    }
}
