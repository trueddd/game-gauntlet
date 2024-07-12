package com.github.trueddd.data

import com.github.trueddd.map.RadioStation

interface RadioCoverage {

    fun stationAt(position: Int): RadioStation
}
