package com.github.trueddd.data

import com.github.trueddd.map.Genre

interface GameGenreDistribution {

    val genres: List<Genre>

    fun genreAtPosition(position: Int): Genre {
        return genres[position - 1]
    }

    fun closestPositionToGenre(anchorPosition: Int, genre: Genre): Int {
        var shift = 1
        while (anchorPosition + shift in genres.indices || anchorPosition - shift in genres.indices) {
            if (genreAtPosition(anchorPosition + shift) == genre) {
                return anchorPosition + shift
            }
            if (genreAtPosition(anchorPosition - shift) == genre) {
                return anchorPosition - shift
            }
            shift++
        }
        throw IllegalStateException("No genres were found")
    }

    fun getStint(index: Int): List<Genre> {
        return genres.subList(index * Genre.entries.size, (index + 1) * Genre.entries.size)
    }
}
