package com.github.trueddd.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class GameGenreDistribution(
    val genres: List<Game.Genre>,
) {

    fun genreAtPosition(position: Int): Game.Genre {
        return genres[position - 1]
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

        val serializer = object : KSerializer<GameGenreDistribution> {

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
    }

    fun getStint(index: Int): List<Game.Genre> {
        return genres.subList(index * Genres.size, (index + 1) * Genres.size)
    }
}
