package com.github.trueddd.map.default

import com.github.trueddd.map.Genre

internal fun getDefaultGenres(): List<Genre> {
    return listOf(
        listOf(1, 5, 4, 2, 6, 3),
        listOf(3, 2, 5, 4, 6, 1),
        listOf(6, 3, 4, 2, 1, 5),
        listOf(1, 2, 3, 5, 4, 6),
        listOf(2, 4, 6, 3, 5, 1),
        listOf(2, 1, 6, 5, 4, 3),
        listOf(1, 6, 2, 5, 3, 4),
        listOf(6, 4, 5, 1, 3, 2),
        listOf(3, 2, 5, 1, 4, 6),
        listOf(5, 3, 4, 6, 2, 1),
        listOf(3, 6, 2, 1, 4, 5),
        listOf(4, 2, 6, 5, 1, 3),
        listOf(1, 6, 3, 4, 5, 2),
        listOf(1, 4, 6, 5, 3, 2),
        listOf(2, 3, 4, 5, 6, 1),
        listOf(5, 2, 1, 3, 6, 4),
        listOf(5, 4, 2, 3, 6, 1),
        listOf(6, 5, 1, 3, 4, 2),
        listOf(4, 5, 3, 6, 1, 2),
        listOf(2, 4, 6, 3, 5, 1),
        listOf(1, 6, 2, 5, 3, 4),
        listOf(6, 5, 1, 4, 2, 3),
        listOf(3, 6, 1, 2, 5, 4),
        listOf(4, 2, 6, 3, 5, 1),
        listOf(3, 1, 2, 4, 6, 5),
    ).map { it + 7 }.flatten().map { Genre.entries[it - 1] }
}
