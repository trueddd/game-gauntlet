package com.github.trueddd.map.default

import com.github.trueddd.map.MapConfig
import com.github.trueddd.map.RadioStation

internal fun getDefaultRadioStations(): Map<IntRange, RadioStation> = mapOf(
    1..12 to RadioStation.Anime,
    13..35 to RadioStation.Christian,
    36..62 to RadioStation.Dacha,
    63..79 to RadioStation.Custom,
    80..99 to RadioStation.Dacha,
    100..123 to RadioStation.Custom,
    124..133 to RadioStation.Christian,
    134..149 to RadioStation.Dacha,
    150..MapConfig.LENGTH to RadioStation.Anime,
)
