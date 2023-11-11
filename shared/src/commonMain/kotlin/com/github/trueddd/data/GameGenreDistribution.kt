package com.github.trueddd.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GameGenreDistribution.Serializer::class)
data class GameGenreDistribution(
    val genres: List<Game.Genre>,
) {

    fun genreAtPosition(position: Int): Game.Genre {
        return genres[position - 1]
    }

    fun closestPositionToGenre(anchorPosition: Int, genre: Game.Genre): Int {
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

    companion object {

        private val Genres = Game.Genre.entries

        fun generateRandom(stintCount: Int): GameGenreDistribution {
            val defaultGenres = Genres - Game.Genre.Special
            return GameGenreDistribution(
                List(stintCount) {
                    defaultGenres.shuffled() + Game.Genre.Special
                }.flatten()
            )
        }
    }

    class Serializer : KSerializer<GameGenreDistribution> {

        override val descriptor = PrimitiveSerialDescriptor("GameGenreDistribution", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): GameGenreDistribution {
            return decoder.decodeString()
                .map { ordinal -> ordinal.digitToInt().let { Game.Genre.entries[it] } }
                .let { GameGenreDistribution(it) }
        }

        override fun serialize(encoder: Encoder, value: GameGenreDistribution) {
            encoder.encodeString(value.genres.joinToString("") { it.ordinal.toString() })
        }
    }

    fun getStint(index: Int): List<Game.Genre> {
        return genres.subList(index * Genres.size, (index + 1) * Genres.size)
    }
}
