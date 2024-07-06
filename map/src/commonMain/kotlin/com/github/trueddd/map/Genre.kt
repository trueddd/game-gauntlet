package com.github.trueddd.map

import com.github.trueddd.map.serialization.enumOrdinalSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Genre.Companion::class)
enum class Genre {
    Runner,
    Business,
    Puzzle,
    PointAndClick,
    Shooter,
    ThreeInRow,
    // TODO: fill up special games
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

    companion object : KSerializer<Genre> by enumOrdinalSerializer()
}
