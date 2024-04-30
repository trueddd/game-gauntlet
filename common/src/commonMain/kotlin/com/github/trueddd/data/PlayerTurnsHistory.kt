package com.github.trueddd.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class PlayerTurnsHistory(
    @SerialName("tu")
    val turns: List<Turn>,
    @SerialName("st")
    val statistics: PlayerStatistics,
) {
    companion object {
        fun default() = PlayerTurnsHistory(emptyList(), PlayerStatistics.default())
    }
}

@Serializable
data class PlayerStatistics(
    val thrownDices: List<Int>,
    val finishedGames: Int,
    val rerolledGames: Int,
    val droppedGames: Int,
) {
    @Transient
    val averageDice: Double = thrownDices.average()

    companion object {
        fun default() = PlayerStatistics(
            thrownDices = emptyList(),
            finishedGames = 0,
            rerolledGames = 0,
            droppedGames = 0
        )
    }
}

@Serializable
data class Turn(
    @SerialName("md")
    val moveDate: Long,
    @Serializable(with = IntRangeSerializer::class)
    @SerialName("mr")
    val moveRange: IntRange?,
    @SerialName("ge")
    val game: GameHistoryEntry?,
) {

    companion object IntRangeSerializer : KSerializer<IntRange> {

        override val descriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): IntRange {
            val (start, end) = decoder.decodeString().split("|").map(String::toInt)
            return start..end
        }

        override fun serialize(encoder: Encoder, value: IntRange) {
            encoder.encodeString("${value.first}|${value.last}")
        }
    }
}
