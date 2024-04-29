package com.github.trueddd.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.theme.Colors
import com.github.trueddd.ui.res.icons.Warning

@Composable
fun WarningTextBlock(
    text: String,
    title: String = "Внимание",
    sideLineWidth: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .drawBehind {
                val borderSizeInPx = sideLineWidth.toPx()
                val x = borderSizeInPx / 2
                drawLine(
                    color = Colors.Warning,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = borderSizeInPx
                )
            }
            .padding(start = sideLineWidth)
    ) {
        val leftSidePadding = 12.dp
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(start = leftSidePadding)
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = Colors.Warning
            )
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = Colors.Warning,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = text,
            modifier = Modifier
                .padding(start = leftSidePadding)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
