package com.github.trueddd.util

import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private fun Long.toTwoDigitsString() = toString().padStart(2, '0')

private val HOUR_IN_MILLIS = 1.hours.inWholeMilliseconds
private val HOUR_IN_MINUTES = 1.hours.inWholeMinutes
private val MINUTE_IN_MILLIS = 1.minutes.inWholeMilliseconds
private val MINUTE_IN_SECONDS = 1.minutes.inWholeSeconds

fun Long.formatAsTimeString(): String {
    val hours = (this / HOUR_IN_MILLIS % HOUR_IN_MINUTES).toString()
    val minutes = (this / MINUTE_IN_MILLIS % MINUTE_IN_SECONDS).toTwoDigitsString()
    val seconds = (this / MINUTE_IN_MILLIS).toTwoDigitsString()
    return "$hours:$minutes:$seconds"
}
