package com.github.trueddd.ui.map

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.theme.Colors
import kotlin.math.roundToInt

@Composable
internal fun GlobalEventCounter(
    pointsAmount: Long,
    pointsCap: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = modifier
            .border(2.dp, Colors.Error, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = buildString {
                append("Собрано баллов на глобальное событие: ")
                append(pointsAmount)
                append(" из ")
                append(pointsCap)
                append(" (")
                append(pointsAmount.toDouble().div(pointsCap).times(100).roundToInt())
                append("%)")
            },
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
