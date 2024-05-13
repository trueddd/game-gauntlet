package com.github.trueddd.data

import com.github.trueddd.actions.GlobalEvent
import kotlinx.serialization.Serializable

@Serializable
data class ScheduledEvent(
    val eventType: GlobalEvent.Type,
    val startTime: Long,
    val epicenterStintIndex: Int,
)
