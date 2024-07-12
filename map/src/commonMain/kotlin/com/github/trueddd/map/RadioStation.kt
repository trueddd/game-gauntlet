package com.github.trueddd.map

import com.github.trueddd.map.serialization.enumOrdinalSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RadioStation.Companion::class)
enum class RadioStation {

    Christian,
    Dacha,
    Anime,
    Custom;

    companion object : KSerializer<RadioStation> by enumOrdinalSerializer()
}
