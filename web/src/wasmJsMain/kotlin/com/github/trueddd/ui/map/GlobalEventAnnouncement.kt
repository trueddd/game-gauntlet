package com.github.trueddd.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.actions.GlobalEvent
import com.github.trueddd.data.ScheduledEvent
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.formatAsTimeString
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun GlobalEventAnnouncement(
    scheduledEvent: ScheduledEvent,
    modifier: Modifier = Modifier,
) {
    var timer by remember { mutableStateOf("") }
    LaunchedEffect(scheduledEvent) {
        while (isActive) {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val remaining = scheduledEvent.startTime - currentTime
            timer = remaining.formatAsTimeString()
            delay(SECOND_IN_MILLIS - (Clock.System.now().toEpochMilliseconds() - currentTime))
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .background(Colors.Error, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = buildString {
                when (scheduledEvent.eventType) {
                    GlobalEvent.Type.Tornado -> "Внимание! Приближается ураган!"
                    GlobalEvent.Type.Nuke -> "Внимание! Ожидается ядерный удар!"
                }.let { append(it) }
                if (timer.isNotEmpty()) {
                    append(" До начала: ")
                    append(timer)
                }
            },
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private val SECOND_IN_MILLIS = 1.seconds.inWholeMilliseconds
