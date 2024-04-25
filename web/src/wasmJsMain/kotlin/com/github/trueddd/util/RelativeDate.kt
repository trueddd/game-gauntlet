package com.github.trueddd.util

import com.github.trueddd.utils.DefaultTimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

sealed class RelativeDate(open val date: LocalDate) {
    companion object {
        fun from(localDateTime: LocalDateTime): RelativeDate {
            val date = localDateTime.date
            val todayDays = Clock.System.now().toLocalDateTime(DefaultTimeZone).date.toEpochDays()
            return when (date.toEpochDays()) {
                todayDays -> Today(date)
                todayDays - 1 -> Yesterday(date)
                else -> Other(date)
            }
        }
    }
    data class Today(override val date: LocalDate) : RelativeDate(date)
    data class Yesterday(override val date: LocalDate) : RelativeDate(date)
    data class Other(override val date: LocalDate) : RelativeDate(date)
}
