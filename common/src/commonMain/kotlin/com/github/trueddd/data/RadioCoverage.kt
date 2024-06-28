package com.github.trueddd.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RadioCoverage.Serializer::class)
data class RadioCoverage(
    val coverage: ArrayList<RadioStation>,
) {

    fun stationAt(position: Int): RadioStation {
        return coverage[position]
    }

    companion object {
        fun generateRandom(
            playableRange: IntRange = GlobalState.PLAYABLE_BOARD_RANGE,
            stintLength: Int = GlobalState.STINT_SIZE,
        ): RadioCoverage {
            val stationsOrder = RadioStation.entries.shuffled()
            val array = ArrayList<RadioStation>(playableRange.count() + 1)
            array.add(0, stationsOrder.first())
            for (index in playableRange) {
                val stintIndex = (index - 1) / stintLength
                val stationIndex = stintIndex % stationsOrder.size
                array.add(index, stationsOrder[stationIndex])
            }
            return RadioCoverage(array)
        }
    }

    class Serializer : KSerializer<RadioCoverage> {

        override val descriptor = PrimitiveSerialDescriptor("RadioCoverage", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): RadioCoverage {
            return decoder.decodeString()
                .map { ordinal -> ordinal.digitToInt().let { RadioStation.entries[it] } }
                .let { RadioCoverage(ArrayList(it)) }
        }

        override fun serialize(encoder: Encoder, value: RadioCoverage) {
            encoder.encodeString(value.coverage.joinToString("") { it.ordinal.toString() })
        }
    }
}
