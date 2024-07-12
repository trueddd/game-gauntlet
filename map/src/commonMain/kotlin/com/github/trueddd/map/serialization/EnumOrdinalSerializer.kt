package com.github.trueddd.map.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.enumEntries

inline fun <reified T : Enum<T>> enumOrdinalSerializer(): KSerializer<T> {
    return object : KSerializer<T> {

        override val descriptor: SerialDescriptor by lazy {
            PrimitiveSerialDescriptor("EnumOrdinalSerializer", PrimitiveKind.INT)
        }

        override fun deserialize(decoder: Decoder): T {
            return decoder.decodeInt().let { enumEntries<T>()[it] }
        }

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeInt(value.ordinal)
        }
    }
}
