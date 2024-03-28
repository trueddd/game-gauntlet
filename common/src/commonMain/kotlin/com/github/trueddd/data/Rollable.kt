package com.github.trueddd.data

/**
 * Describes info about rollable item (wheel item, game or player),
 * that should be displayed in wheel.
 */
interface Rollable {
    val name: String
    val description: String
    val color: Long
}
