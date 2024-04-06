package com.github.trueddd.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class PlayerTurnsHistory(
    @SerialName("tu")
    val turns: List<Turn>,
)

@Serializable
data class Turn(
    @SerialName("md")
    val moveDate: Long,
    @Serializable(with = IntRangeSerializer::class)
    @SerialName("mr")
    val moveRange: IntRange?,
    @SerialName("ge")
    val game: GameHistoryEntry,
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
